package gov.usgs.volcanoes.winston.server.cmd;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;

import gov.usgs.math.DownsamplingType;
import gov.usgs.net.NetTools;
import gov.usgs.plot.data.RSAMData;
import gov.usgs.util.Util;
import gov.usgs.util.UtilException;
import gov.usgs.volcanoes.winston.db.WinstonDatabase;
import gov.usgs.volcanoes.winston.server.WWS;
import gov.usgs.volcanoes.winston.server.WWSCommandString;

/**
 *
 * @author Dan Cervelli
 */
public class GetSCNLRSAMRawCommand extends BaseCommand {
  public GetSCNLRSAMRawCommand(final NetTools nt, final WinstonDatabase db, final WWS wws) {
    super(nt, db, wws);
  }

  public void doCommand(final Object info, final SocketChannel channel) {
    final WWSCommandString cmd = new WWSCommandString((String) info);
    if (!cmd.isLegalSCNLTT(10) || Double.isNaN(cmd.getDouble(8))
        || cmd.getInt(9) == Integer.MIN_VALUE)
      return; // malformed command

    RSAMData rsam = null;
    double t1 = Double.NaN;
    double t2 = Double.NaN;

    try {
      t1 = cmd.getT1(true);
      t1 = timeOrMaxDays(t1);

      t2 = cmd.getT2(true);
      t2 = timeOrMaxDays(t2);

      final int ds = (int) cmd.getDouble(8);
      DownsamplingType dst = DownsamplingType.MEAN;
      if (ds < 2)
        dst = DownsamplingType.NONE;

      rsam = data.getRSAMData(cmd.getWinstonSCNL(), t1, t2, 0, dst, ds);
    } catch (final UtilException e) {
      // can I do anything here?
    }
    ByteBuffer bb = null;
    if (rsam != null && rsam.rows() > 0)
      bb = (ByteBuffer) rsam.toBinary().flip();
    final int bytes = writeByteBuffer(cmd.getID(), bb, cmd.getInt(9) == 1, channel);

    final String time = Util.j2KToDateString(t1) + " - " + Util.j2KToDateString(t2);
    wws.log(Level.FINER,
        "GETSCNLRSAMRAW " + cmd.getWinstonSCNL() + ": " + time + ", " + bytes + " bytes.", channel);
  }
}
