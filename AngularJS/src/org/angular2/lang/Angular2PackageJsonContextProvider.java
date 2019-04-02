// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.angular2.lang;

import com.intellij.javascript.nodejs.PackageJsonData;
import com.intellij.javascript.nodejs.packageJson.PackageJsonFileManager;
import com.intellij.lang.javascript.buildTools.npm.PackageJsonUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.util.CachedValueProvider;
import org.jetbrains.annotations.NonNls;

import static com.intellij.lang.javascript.library.JSLibraryUtil.NODE_MODULES;
import static org.angular2.lang.html.psi.impl.Angular2HtmlReferenceVariableImpl.ANGULAR_CORE_PACKAGE;

public class Angular2PackageJsonContextProvider implements Angular2ContextProvider {

  @NonNls private static final String NODE_MODULE_ANGULAR_CORE_PATH = "/" + NODE_MODULES + "/" + ANGULAR_CORE_PACKAGE + "/";

  @Override
  public CachedValueProvider.Result<Boolean> isAngular2Context(PsiDirectory psiDir) {
    VirtualFile dir = psiDir.getVirtualFile();
    PackageJsonFileManager manager = PackageJsonFileManager.getInstance(psiDir.getProject());
    String dirPath = dir.getPath() + "/";
    boolean result = false;
    for (VirtualFile config : manager.getValidPackageJsonFiles()) {
      String configDirPath = config.getParent().getPath() + "/";
      if (configDirPath.endsWith(NODE_MODULE_ANGULAR_CORE_PATH)) {
        if (dirPath.startsWith(configDirPath.substring(0, configDirPath.length() - NODE_MODULE_ANGULAR_CORE_PATH.length()) + "/")) {
          result = true;
          break;
        }
      }
      else if (dirPath.startsWith(configDirPath)) {
        PackageJsonData data = PackageJsonUtil.getOrCreateData(config);
        if (data.isDependencyOfAnyType(ANGULAR_CORE_PACKAGE)) {
          result = true;
          break;
        }
      }
    }
    return CachedValueProvider.Result.create(
      result, PackageJsonFileManager.getInstance(psiDir.getProject()).getModificationTracker());
  }
}
