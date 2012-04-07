package com.cloudera.whirr.cm;

import static org.apache.whirr.RolePredicates.role;
import static org.jclouds.scriptbuilder.domain.Statements.call;

import com.google.common.collect.Iterables;

import java.io.IOException;
import java.util.Set;

import org.apache.whirr.Cluster;
import org.apache.whirr.Cluster.Instance;
import org.apache.whirr.service.ClusterActionEvent;
import org.apache.whirr.service.ClusterActionHandlerSupport;
import org.apache.whirr.service.FirewallManager.Rule;

public class CmServerHandler extends ClusterActionHandlerSupport {

  public static final String ROLE = "cmserver";
  private static final int CLIENT_PORT = 7180;
  
  @Override public String getRole() { return ROLE; }
  
  @Override
  protected void beforeBootstrap(ClusterActionEvent event) throws IOException {
    addStatement(event, call("install_cm"));
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
    System.out.printf("Cloudera Manager Admin Console available at http://%s:%s\n",
        masterAddress, CLIENT_PORT);
    
    System.out.println("Nodes in cluster (copy into text area when setting up the cluster):");
    Set<Instance> nodes = cluster.getInstancesMatching(role(CmNodeHandler.ROLE));
    for (Instance i : nodes) {
      System.out.println(i.getPrivateIp());
    }
    System.out.println();
    
    System.out.println("Authentication Method (choose \"All hosts accept same public key\"):");
    System.out.printf("User name (choose \"another user\"): %s\n",
        System.getProperty("user.name"));
    String privateKey = event.getClusterSpec().getPrivateKeyFile().getCanonicalPath();
    System.out.printf("Public key file: %s\n", privateKey + ".pub");
    System.out.printf("Private key file: %s (empty passphrase)\n", privateKey);
    System.out.println();

    if (nodes.size() > 0) {
      // assumes set has stable ordering
      System.out.printf("Hue UI will be available at http://%s:8888\n",
          Iterables.get(nodes, 0).getPublicHostName());
    }
  }
}
