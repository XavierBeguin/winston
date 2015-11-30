package gov.usgs.volcanoes.winston.in.ew;

import gov.usgs.earthworm.message.TraceBuf;
import gov.usgs.util.CurrentTime;

/**
 *
 * $Log: not supported by cvs2svn $
 * 
 * @author Dan Cervelli
 */
public class MaxDaysFilter extends TraceBufFilter {
  public MaxDaysFilter() {}

  @Override
  public boolean match(final TraceBuf tb, final Options options) {
    if (options.maxDays <= 0)
      return false;

    final double t = tb.getStartTimeJ2K();
    final double ds = CurrentTime.getInstance().nowJ2K() - t;
    return (ds > (options.maxDays * 86400.0));
  }

  @Override
  public String toString() {
    return "MaxDaysFilter";
  }
}
