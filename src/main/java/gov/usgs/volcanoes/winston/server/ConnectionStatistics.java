/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.winston.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usgs.volcanoes.core.time.Time;
import gov.usgs.volcanoes.core.time.TimeSpan;
import gov.usgs.volcanoes.core.util.StringUtils;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;

/**
 * Statistics of connections to a wave server.
 * 
 * @author Tom Parker
 *
 */
public class ConnectionStatistics {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionStatistics.class);
  private static final String HEADER_FORMAT = "%-25s %-22s %-22s %-11s %-11s%n";

  private final AtomicLong connectionCount;
  private final AtomicLong wwsCount;
  private final AtomicLong httpCount;
  private final AtomicLong openCount;
  private Map<InetSocketAddress, Connection> connectionMap;

  /**
   * Constructor.
   */
  public ConnectionStatistics() {
    LOGGER.debug("Creating new connection stats");
    connectionCount = new AtomicLong(0);
    wwsCount = new AtomicLong(0);
    httpCount = new AtomicLong(0);
    openCount = new AtomicLong(0);
    connectionMap = Collections.synchronizedMap(new HashMap<InetSocketAddress, Connection>());

  }

  /**
   * return connection count.
   * @return connection count
   */
  public long getCount() {
    return connectionCount.get();
  }

  /**
   * Increment the HTTP count.
   * 
   * @param socketAddress address of new connection
   */
  public void incrHttpCount(SocketAddress socketAddress) {
    httpCount.incrementAndGet();
    connectionMap.get(socketAddress).lastTime(System.currentTimeMillis());
  }

  /**
   * Return HTTP connection count.
   * 
   * @return HTTP connection count
   */
  public long getHttpCount() {
    return httpCount.get();
  }

  /**
   * Increment WWS count.
   * 
   * @param socketAddress address of new connection
   */
  public void incrWwsCount(SocketAddress socketAddress) {
    wwsCount.incrementAndGet();
    connectionMap.get(socketAddress).lastTime(System.currentTimeMillis());
  }

  /**
   * Return WWS connnection count.
   * 
   * @return count
   */
  public long getWwsCount() {
    return wwsCount.get();
  }

  /** 
   * Increment open connection count.
   */
  public void incrOpenCount() {
    openCount.incrementAndGet();
    connectionCount.incrementAndGet();
  }

  /**
   * decrement connection count.
   */
  public void decrOpenCount() {
    openCount.decrementAndGet();
  }

  /**
   * Find open connection count.
   * 
   * @return connection count
   */
  public long getOpen() {
    return openCount.get();
  }

  /**
   * Add connection.
   * 
   * @param remoteAddress remote address
   * @param trafficCounter counter
   */
  public void mapChannel(InetSocketAddress remoteAddress,
      ChannelTrafficShapingHandler trafficCounter) {
    LOGGER.debug("mapping " + remoteAddress);
    connectionMap.put(remoteAddress, new Connection(remoteAddress.toString(), trafficCounter));
  }

  /**
   * Remove connection.
   * 
   * @param remoteAddress address to remove
   */
  public void unmapChannel(InetSocketAddress remoteAddress) {
    connectionMap.remove(remoteAddress);
  }

  /**
   * Get formatted string of connections.
   * 
   * @param s sort order
   * @return connection string
   */
  public String printConnections(String s) {
    String header = String.format(HEADER_FORMAT, "[A]ddress", "[C]onnect duration", "[I]dle time",
        "[R]X", "[T]X");
    StringBuffer sb = new StringBuffer();
    sb.append("------- Connections --------\n");
    sb.append(header);

    char col = 'T';
    if (s.length() > 1)
      col = s.charAt(1);

    Connection.SortField field = Connection.SortField.parse(col);
    Connection.SortOrder order =
        s.endsWith("-") ? Connection.SortOrder.DESCENDING : Connection.SortOrder.ASCENDING;

    List<Connection> connections = new ArrayList<Connection>(connectionMap.size());
    connections.addAll(connectionMap.values());
    Collections.sort(connections, Connection.getComparator(field, order));

    long now = System.currentTimeMillis();
    for (Connection connection : connections) {
      TimeSpan connectTimeSpan = new TimeSpan(connection.connectTime(), now);
      TimeSpan idleTimeSpan = new TimeSpan(connection.lastTime(), now);

      sb.append(String.format(HEADER_FORMAT, connection.address(),
          connectTimeSpan.span(),
          idleTimeSpan.span(),
          StringUtils.numBytesToString(connection.cumulativeReadBytes()),
          StringUtils.numBytesToString(connection.cumulativeWrittenBytes())));

    }
    sb.append(header);
    sb.append("\n\n");
    sb.append("Total Connections : ").append(connectionCount).append('\n');
    sb.append("Open Connections  : ").append(openCount).append('\n');
    sb.append("WWS Commands      : ").append(wwsCount).append('\n');
    sb.append("HTTP commands     : ").append(httpCount).append('\n');
    return sb.toString();
  }

}
