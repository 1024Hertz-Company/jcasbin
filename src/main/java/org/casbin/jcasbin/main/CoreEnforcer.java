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

package org.casbin.jcasbin.main;

import org.casbin.jcasbin.effect.DefaultEffector;
import org.casbin.jcasbin.effect.Effector;
import org.casbin.jcasbin.persist.Watcher;
import org.casbin.jcasbin.persist.file_adapter.FileAdapter;
import org.casbin.jcasbin.model.FunctionMap;
import org.casbin.jcasbin.model.Model;
import org.casbin.jcasbin.persist.Adapter;
import org.casbin.jcasbin.rbac.DefaultRoleManager;
import org.casbin.jcasbin.rbac.RoleManager;
import org.casbin.jcasbin.util.Util;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * CoreEnforcer is the main interface for authorization enforcement and policy management.
 */
public class CoreEnforcer {
    private String modelPath;
    public Model model;
    private Map<String, Method> fm;
    private Effector eft;

    Adapter adapter;
    Watcher watcher;
    private RoleManager rm;

    private boolean enabled;
    boolean autoSave;
    boolean autoBuildRoleLinks;

    /**
     * CoreEnforcer is the default constructor.
     */
    public CoreEnforcer() {
    }

    /**
     * CoreEnforcer initializes an enforcer with a model file and a policy file.
     */
    public CoreEnforcer(String modelPath, String policyFile) {
        this.modelPath = modelPath;

        adapter = new FileAdapter(policyFile);

        initialize();

        if (!this.modelPath.equals("")) {
            loadModel();
            loadPolicy();
        }
    }

    /**
     * CoreEnforcer initializes an enforcer with a database adapter.
     */
    public CoreEnforcer(String modelPath, Adapter adapter) {
        this.modelPath = modelPath;

        this.adapter = adapter;

        initialize();

        if (!this.modelPath.equals("")) {
            loadModel();
            loadPolicy();
        }
    }

    /**
     * CoreEnforcer initializes an enforcer with a model and a database adapter.
     */
    public CoreEnforcer(Model m, Adapter adapter) {
        modelPath = "";
        this.adapter = adapter;

        model = m;
        model.printModel();
        fm = FunctionMap.loadFunctionMap();

        initialize();

        if (this.adapter != null) {
            loadPolicy();
        }
    }

    private void initialize() {
        rm = new DefaultRoleManager(10);
        eft = new DefaultEffector();
        watcher = null;

        enabled = true;
        autoSave = true;
        autoBuildRoleLinks = true;
    }

    /**
     * newModel creates a model.
     */
    private Model newModel() {
        Model model = new Model();

        return model;
    }

    /**
     * newModel creates a model.
     */
    private Model newModel(String text) {
        Model model = new Model();

        model.loadModelFromText(text);

        return model;
    }

    /**
     * loadModel reloads the model from the model CONF file.
     * Because the policy is attached to a model, so the policy is invalidated and needs to be reloaded by calling LoadPolicy().
     */
    public void loadModel() {
        model = newModel();
        model.loadModel(this.modelPath);
        model.printModel();
        fm = FunctionMap.loadFunctionMap();
    }

    /**
     * getModel gets the current model.
     */
    public Model getModel() {
        return model;
    }

    /**
     * setModel sets the current model.
     */
    public void setModel(Model model) {
        this.model = model;
        fm = FunctionMap.loadFunctionMap();
    }

    /**
     * getAdapter gets the current adapter.
     */
    public Adapter getAdapter() {
        return adapter;
    }

    /**
     * setAdapter sets the current adapter.
     */
    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    /**
     * setWatcher sets the current watcher.
     */
    public void setWatcher(Watcher watcher) {
        this.watcher = watcher;
        watcher.setUpdateCallback(loadPolicy());
    }

    /**
     * SetRoleManager sets the current role manager.
     */
    public void setRoleManager(RoleManager rm) {
        this.rm = rm;
    }

    /**
     * setEffector sets the current effector.
     */
    public void setEffector(Effector eft) {
        this.eft = eft;
    }

    /**
     * clearPolicy clears all policy.
     */
    public void clearPolicy() {
        model.clearPolicy();
    }

    /**
     * loadPolicy reloads the policy from file/database.
     */
    public void loadPolicy() {
        model.clearPolicy();
        adapter.loadPolicy(model);

        model.printPolicy();
        if (autoBuildRoleLinks) {
            buildRoleLinks();
        }
    }

    /**
     * loadFilteredPolicy reloads a filtered policy from file/database.
     */
    public void loadFilteredPolicy(Object filter) {
    }

    /**
     * isFiltered returns true if the loaded policy has been filtered.
     */
    public boolean isFiltered() {
        return false;
    }

    /**
     * savePolicy saves the current policy (usually after changed with Casbin API) back to file/database.
     */
    public void savePolicy() {
        if (isFiltered()) {
            throw new Error("cannot save a filtered policy");
        }

        adapter.savePolicy(model);
        if (watcher != null) {
            watcher.update();
        }
    }

    /**
     * enableEnforce changes the enforcing state of Casbin, when Casbin is disabled, all access will be allowed by the Enforce() function.
     */
    public void enableEnforce(boolean enable) {
        this.enabled = enable;
    }

    /**
     * enableLog changes whether to print Casbin log to the standard output.
     */
    public void enableLog(boolean enable) {
        Util.enableLog = enable;
    }

    /**
     * enableAutoSave controls whether to save a policy rule automatically to the adapter when it is added or removed.
     */
    public void enableAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    /**
     * enableAutoBuildRoleLinks controls whether to save a policy rule automatically to the adapter when it is added or removed.
     */
    public void enableAutoBuildRoleLinks(boolean autoBuildRoleLinks) {
        this.autoBuildRoleLinks = autoBuildRoleLinks;
    }

    /**
     * buildRoleLinks manually rebuild the
     * role inheritance relations.
     */
    public void buildRoleLinks() {
        rm.clear();
        model.buildRoleLinks(rm);
    }

    /**
     * enforce decides whether a "subject" can access a "object" with the operation "action", input parameters are usually: (sub, obj, act).
     */
    public boolean enforce(String... rvals) {
        return true;
    }
}
