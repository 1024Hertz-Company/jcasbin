// Copyright 2017 The casbin Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.casbin.jcasbin.model;

import org.casbin.jcasbin.util.BuiltInFunctions;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * FunctionMap represents the collection of Function.
 */
public class FunctionMap {
    /**
     * Method represents a function that is used in the matchers, used to get attributes in ABAC.
     */
    private Map<String, BiFunction<String, String, Boolean>> fm;

    /**
     * addFunction adds an expression function.
     */
    public void addFunction(String name, BiFunction<String, String, Boolean> function) {
        fm.put(name, function);
    }

    /**
     * loadFunctionMap loads an initial function map.
     */
    public static FunctionMap loadFunctionMap() {
        // Map<String, Method> fm = new HashMap<>();

        FunctionMap fm = new FunctionMap();

        return fm;
    }
}
