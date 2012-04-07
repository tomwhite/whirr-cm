package com.cloudera.whirr.cm;

import static org.apache.whirr.RolePredicates.role;

import java.io.IOException;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.whirr.ClusterSpec;
import org.apache.whirr.service.ClusterActionEvent;
import org.apache.whirr.service.ClusterActionHandlerSupport;
import org.apache.whirr.service.FirewallManager.Rule;

public class CmNodeHandler extends ClusterActionHandlerSupport {

  public static final String ROLE = "cmnode";
  private static final String PORTS = "cmnode.ports";
  
  @Override public String getRole() { return ROLE; }
  
  private Configuration getConfiguration(ClusterSpec spec)
      throws IOException {
    return getConfiguration(spec, "whirr-cm-default.properties");
  }
  
  @Override
  protected void beforeConfigure(ClusterActionEvent event) throws IOException,
      InterruptedException {
    List<?> ports = getConfiguration(event.getClusterSpec()).getList(PORTS);
    if (ports != null) {
      for (Object port : ports) {
        event.getFirewallManager().addRule(
            Rule.create().destination(role(ROLE)).port(Integer.parseInt(port.toString()))
        );
      }
    }
  }

}
