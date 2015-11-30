package gov.usgs.volcanoes.winston.server.cmd.http.fdsn.dataselect;

import gov.usgs.net.NetTools;
import gov.usgs.volcanoes.winston.db.WinstonDatabase;
import gov.usgs.volcanoes.winston.server.WWS;
import gov.usgs.volcanoes.winston.server.cmd.http.fdsn.command.FdsnUsageCommand;

/**
 *
 * @author Tom Parker
 *
 */
public class FdsnDataselectUsage extends FdsnUsageCommand implements FdsnDataselectService {

  public FdsnDataselectUsage(final NetTools nt, final WinstonDatabase db, final WWS wws) {
    super(nt, db, wws);
    UrlBuillderTemplate = "www/fdsnws/dataselect_UrlBuilder";
    InterfaceDescriptionTemplate = "www/fdsnws/dataselect_InterfaceDescription";
  }

  @Override
  public String getCommand() {
    return "/fdsnws/dataselect/1";
  }
}
