package gov.usgs.volcanoes.winston.server.cmd.http.fdsn.station;

import gov.usgs.net.NetTools;
import gov.usgs.volcanoes.winston.db.WinstonDatabase;
import gov.usgs.volcanoes.winston.server.WWS;
import gov.usgs.volcanoes.winston.server.cmd.http.fdsn.command.FdsnUsageCommand;

/**
 *
 * @author Tom Parker
 *
 */
public class FdsnStationUsage extends FdsnUsageCommand implements FdsnStationService {

  public FdsnStationUsage(final NetTools nt, final WinstonDatabase db, final WWS wws) {
    super(nt, db, wws);
    UrlBuillderTemplate = "www/fdsnws/station_UrlBuilder";
    InterfaceDescriptionTemplate = "www/fdsnws/station_InterfaceDescription";
  }

  @Override
  public String getCommand() {
    return "/fdsnws/station/1";
  }
}