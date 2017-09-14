/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.winston.server.wws.cmd;

import gov.usgs.volcanoes.core.contrib.HashCodeUtil;
import gov.usgs.volcanoes.winston.server.MalformedCommandException;
import gov.usgs.volcanoes.winston.server.wws.WwsCommandString;

/**
 * 
 * @author Tom Parker
 *
 */
public class GetScnRawCommand extends GetScnlRawCommand {
  /**
   * Constructor.
   */
  public GetScnRawCommand() {
    super();
  }
  
  protected void parseCommand(WwsCommandString cmd) throws MalformedCommandException {
    int hash = HashCodeUtil.hash(HashCodeUtil.SEED, cmd);
    if (cmdHash == Integer.MIN_VALUE || cmdHash != hash) {
      scnl = cmd.getScnl();
      timeSpan = cmd.getEwTimeSpan(WwsCommandString.NO_LOCATION);      
    }
  }

}
