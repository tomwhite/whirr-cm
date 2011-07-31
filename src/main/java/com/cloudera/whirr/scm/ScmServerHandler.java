package com.cloudera.whirr.scm;

import static org.apache.whirr.RolePredicates.role;
import static org.jclouds.scriptbuilder.domain.Statements.call;

import java.io.IOException;

import org.apache.whirr.Cluster;
import org.apache.whirr.Cluster.Instance;
import org.apache.whirr.service.ClusterActionEvent;
import org.apache.whirr.service.ClusterActionHandlerSupport;
import org.apache.whirr.service.FirewallManager.Rule;

public class ScmServerHandler extends ClusterActionHandlerSupport {

  public static final String ROLE = "scmserver";
  private static final int CLIENT_PORT = 7180;
  
  @Override public String getRole() { return ROLE; }
  
  @Override
  protected void beforeBootstrap(ClusterActionEvent event) throws IOException {
    addStatement(event, call("install_scm"));
  }

  @Override
  protected void beforeConfigure(ClusterActionEvent event) throws IOException,
      InterruptedException {
    event.getFirewallManager().addRule(
        Rule.create().destination(role(ROLE)).port(CLIENT_PORT)
    );
  }
  
  @Override
  protected void afterConfigure(ClusterActionEvent event) throws IOException,
      InterruptedException {
    Cluster cluster = event.getCluster();
    Instance master = cluster.getInstanceMatching(role(ROLE));
    String masterAddress = master.getPublicAddress().getHostName();
    System.out.printf("SCM Admin Console available at http://%s:%s\n",
        masterAddress, CLIENT_PORT);
  }

}
