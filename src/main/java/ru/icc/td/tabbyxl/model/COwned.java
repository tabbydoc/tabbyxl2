/*
 * Copyright 2015-18 Alexey O. Shigarov (shigarov@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.icc.td.tabbyxl.model;

public abstract class COwned {
    private CTable owner;

    protected CTable getOwner() {
        return owner;
    }

    protected void setOwner(CTable owner) {
        if (null == owner)
            throw new NullPointerException("The owner cannot be null");
        this.owner = owner;
    }

    protected COwned(CTable owner) {
        super();
        setOwner(owner);
    }
}
