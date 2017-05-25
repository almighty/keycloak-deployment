/*
 *  Copyright 2016 Red Hat, Inc. and/or its affiliates
 *  and other contributors as indicated by the @author tags.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.keycloak.authorization.policy.provider.aggregated;

import org.keycloak.authorization.model.Policy;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.authorization.policy.provider.PolicyProviderAdminService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class AggregatePolicyAdminResource implements PolicyProviderAdminService {

    private final ResourceServer resourceServer;

    public AggregatePolicyAdminResource(ResourceServer resourceServer) {
        this.resourceServer = resourceServer;
    }

    @Override
    public void onCreate(Policy policy) {
        verifyCircularReference(policy, new ArrayList<>());
    }

    @Override
    public void onUpdate(Policy policy) {
        verifyCircularReference(policy, new ArrayList<>());
    }

    private void verifyCircularReference(Policy policy, List<String> ids) {
        if (!policy.getType().equals("aggregate")) {
            return;
        }

        if (ids.contains(policy.getId())) {
            throw new RuntimeException("Circular reference found [" + policy.getName() + "].");
        }

        ids.add(policy.getId());

        for (Policy associated : policy.getAssociatedPolicies()) {
            verifyCircularReference(associated, ids);
        }
    }

    @Override
    public void onRemove(Policy policy) {

    }
}
