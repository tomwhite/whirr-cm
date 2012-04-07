package com.cloudera.whirr.cm;

import static org.apache.whirr.RolePredicates.role;
import static org.jclouds.scriptbuilder.domain.Statements.call;

import java.io.IOException;

import org.apache.whirr.Cluster;
import org.apache.whirr.Cluster.Instance;
import org.apache.whirr.service.ClusterActionEvent;
import org.apache.whirr.service.ClusterActionHandlerSupport;

public class CdhClientHandler extends ClusterActionHandlerSupport {

  public static final String ROLE = "cdhclient";
  
  @Override public String getRole() { return ROLE; }
  
  @Override
  protected void beforeBootstrap(ClusterActionEvent event) throws IOException {
    addStatement(event, call("install_cdh"));
  }
  
  @Override
  protected void afterConfigure(ClusterActionEvent event) throws IOException,
      InterruptedException {
    Cluster cluster = event.getCluster();
    Instance client = cluster.getInstanceMatching(role(ROLE));
    String clientAddress = client.getPublicAddress().getHostName();
    System.out.printf("CDH client machine available at %s over SSH.\n",
        clientAddress);
  }

}
