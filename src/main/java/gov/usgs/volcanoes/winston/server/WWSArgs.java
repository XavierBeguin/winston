/**
 * I waive copyright and related rights in the this work worldwide
 * through the CC0 1.0 Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.winston.server;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.Switch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usgs.volcanoes.core.args.Args;
import gov.usgs.volcanoes.core.args.Arguments;
import gov.usgs.volcanoes.core.args.decorator.ConfigFileArg;
import gov.usgs.volcanoes.core.args.decorator.VerboseArg;

/**
 * Argument processor for WWS.
 *
 * @author Tom Parker
 */
public class WWSArgs {
  private static final Logger LOGGER = LoggerFactory.getLogger(WWSArgs.class);

  private static final String DEFAULT_CONFIG_FILENAME = "WWS.config";
  private static final String EXPLANATION = "I am the Winston wave server\n";

  private static final String PROGRAM_NAME = "java -jar gov.usgs.volcanoes.winston.server.WWS";

  /** my config file. */
  public final String configFileName;

  /** if true be verbose */
  public final boolean isVerbose;

  /**
   * Class constructor.
   * 
   * @param commandLineArgs the command line arguments
   * @throws Exception when things go wrong
   */
  public WWSArgs(final String[] commandLineArgs) throws Exception {

    Arguments args = null;
    args = new Args(PROGRAM_NAME, EXPLANATION, null);
    args = new ConfigFileArg(DEFAULT_CONFIG_FILENAME, args);
    args = new VerboseArg(args);

    JSAPResult jsapResult = null;
    jsapResult = args.parse(commandLineArgs);

    isVerbose = jsapResult.getBoolean("verbose");
    configFileName = jsapResult.getString("config-filename");
    LOGGER.debug("Setting: verbose={}", isVerbose);
  }
}
