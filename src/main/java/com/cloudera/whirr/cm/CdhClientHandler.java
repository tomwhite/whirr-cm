/**
 * Licensed to Cloudera, Inc. under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  Cloudera, Inc. licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
  	addStatement(event, call("configure_hostnames"));
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
