/*
 * Copyright (c) 2013-2015 Cinchapi, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cinchapi.concourse.server.distributed;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.cinchapi.concourse.util.TLists;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Utility functions for {@link Cohort cohorts}.
 * 
 * @author Jeff Nelson
 */
public final class Cohorts {

    @SuppressWarnings("unchecked")
    public static <T> Set<Cohort<T>> electRandomCohorts(int cohortSize,
            T... candidates) {
        Preconditions.checkArgument(cohortSize < candidates.length,
                "Cannot elect a cohort with a size greater "
                        + "than the number of candidates");
        Random random = new Random();
        int size = candidates.length;
        int numFollowers = cohortSize - 1;
        Set<Cohort<T>> cohorts = new HashSet<Cohort<T>>(size);
        List<T> pool = Lists.newArrayListWithCapacity(size * cohortSize);
        List<T> followers = Lists.newArrayListWithCapacity(numFollowers);
        while (cohorts.size() != size) {
            // Populate the pool
            pool.clear();
            for (T candidate : candidates) {
                for (int i = 0; i < cohortSize; ++i) {
                    pool.add(candidate);
                }
            }
            Collections.shuffle(pool);
            // Create cohorts
            cohorts.clear();
            for (T candidate : candidates) {
                followers.clear();
                for (int i = 0; i < numFollowers; ++i) {
                    T follower = null;
                    int slot = -1;
                    while (follower == null || follower.equals(candidate)
                            || followers.contains(follower)) {
                        slot = random.nextInt(pool.size());
                        follower = pool.get(slot);
                    }
                    followers.add(follower);
                    pool.remove(slot);
                }
                Cohort<T> cohort = Cohort.<T> create(candidate,
                        TLists.toArrayCasted(followers));
                if(!cohorts.add(cohort)) {
                    // This means we have a duplicate cohort, so break out of
                    // the loop early and retry.
                    break;
                }
            }
        }
        return cohorts;
    }

    private Cohorts() {/* noop */}

}
