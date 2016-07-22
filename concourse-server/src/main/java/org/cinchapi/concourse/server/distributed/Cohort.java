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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;

/**
 * A {@link Cohort} is a group of distinct elements where one is the "founder"
 * and the rest are followers.
 * 
 * <p>
 * <strong>NOTE:</strong> Two cohorts with the same members are considered equal,
 * even if they have different founders.
 * </p>
 * 
 * @author Jeff Nelson
 */
@Immutable
public class Cohort<T> {

    /**
     * Return a {@link Cohort} that contains the {@code founder} and all of the
     * {@code followers} as members.
     * 
     * @param founder
     * @param followers
     * @return the Cohort
     */
    @SafeVarargs
    public static <T> Cohort<T> create(T founder, T... followers) {
        return new Cohort<T>(founder, followers);
    }

    /**
     * The member that is designated as the founder of the {@link Cohort}.
     */
    private final T founder;

    /**
     * All the members of the {@link Cohort}, including the {@link #founder}.
     */
    private final Set<T> members;

    /**
     * Construct a new instance.
     * 
     * @param founder
     * @param followers
     */
    @SafeVarargs
    private Cohort(T founder, T... followers) {
        int size = followers.length + 1;
        this.founder = founder;
        this.members = new HashSet<T>(size);
        members.add(founder);
        for (T follower : followers) {
            members.add(follower);
        }
        Preconditions.checkState(members.size() == size,
                "A %s cannot contain duplicate members", this.getClass()
                        .getSimpleName());;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Cohort) {
            return members.equals(((Cohort) obj).members);
        }
        else {
            return false;
        }
    }

    /**
     * Return the {@link #founder} of the {@link Cohort}.
     * 
     * @return the founder
     */
    public T getFounder() {
        return founder;
    }

    /**
     * Return a set that contains all the members of the {@link Cohort}.
     * 
     * @return the member set
     */
    public Set<T> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    @Override
    public int hashCode() {
        return members.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append("(");
        sb.append("Founder = ");
        sb.append(founder);
        sb.append(" | Followers = [");
        for(T member : members){
            if(!member.equals(founder)){
                sb.append(member);
                sb.append(", ");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        sb.append(")");
        return sb.toString();
    }

}
