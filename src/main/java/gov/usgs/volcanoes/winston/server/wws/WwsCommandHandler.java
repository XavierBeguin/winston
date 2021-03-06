/**
 * I waive copyright and related rights in the this work worldwide
 * through the CC0 1.0 Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.winston.server.wws;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usgs.volcanoes.core.configfile.ConfigFile;
import gov.usgs.volcanoes.winston.server.ConnectionStatistics;
import gov.usgs.volcanoes.winston.server.WinstonDatabasePool;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

/**
 * 
 * 
 * @author Tom Parker
 *
 */
public class WwsCommandHandler extends SimpleChannelInboundHandler<WwsCommandString> {

  private static final Logger LOGGER = LoggerFactory.getLogger(WwsCommandHandler.class);

  private final WinstonDatabasePool winstonDatabasePool;
  private ConnectionStatistics connectionStatistics;

  private static final AttributeKey<ConnectionStatistics> connectionStatsKey;

  static {
    connectionStatsKey = AttributeKey.valueOf("connectionStatistics");
  }

  /**
   * Constructor.
   * 
   * @param configFile my config file
   * @param winstonDatabasePool my database pool
   */
  public WwsCommandHandler(ConfigFile configFile, WinstonDatabasePool winstonDatabasePool) {
    this.winstonDatabasePool = winstonDatabasePool;
  }

  @Override
  public void channelRead0(ChannelHandlerContext ctx, WwsCommandString request) throws Exception {
    connectionStatistics = ctx.channel().attr(connectionStatsKey).get();

    try {
      final WwsBaseCommand wwsWorker = WwsCommandFactory.get(winstonDatabasePool, request);
      connectionStatistics.incrWwsCount(ctx.channel().remoteAddress());
      wwsWorker.respond(ctx, request);
    } catch (final UnsupportedCommandException e) {
      LOGGER.info(e.getLocalizedMessage());
      ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    // Normal and expected client connections can cause both of these. Nothing to be done about it
    if (!(cause instanceof IOException || cause instanceof NullPointerException)) {
      try {
        LOGGER.error("Exception caught in WwsCommandHandler while servicing {}: {} ({})",
            ctx.channel().remoteAddress(), cause.getClass().getName(), cause.getMessage());
        LOGGER.error("exception: ", cause);
      } catch (Exception e) {
        LOGGER.error("Exception caught catching exception. ({})", e.getLocalizedMessage());
      }
    }
    ctx.close();
  }
}
