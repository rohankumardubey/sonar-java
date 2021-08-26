/*
 * SonarQube Java
 * Copyright (C) 2012-2021 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.java.checks;

import java.util.Collections;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.java.checks.helpers.QuickFixHelper;
import org.sonar.java.reporting.JavaQuickFix;
import org.sonar.java.reporting.JavaTextEdit;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaVersion;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.ModifiersTree;
import org.sonar.plugins.java.api.tree.Position;
import org.sonar.plugins.java.api.tree.Tree;

@Rule(key = "S1161")
public class OverrideAnnotationCheck extends IssuableSubscriptionVisitor {

  @Override
  public List<Tree.Kind> nodesToVisit() {
    return Collections.singletonList(Tree.Kind.METHOD);
  }

  @Override
  public void visitNode(Tree tree) {
    if (isExcludedByVersion(context.getJavaVersion())) {
      return;
    }
    MethodTree methodTree = (MethodTree) tree;
    Symbol.MethodSymbol methodSymbol = methodTree.symbol();
    List<Symbol.MethodSymbol> overriddenSymbols = methodSymbol.overriddenSymbols();
    if (overriddenSymbols.isEmpty()) {
      return;
    }
    Symbol.MethodSymbol overriddenSymbol = overriddenSymbols.get(0);
    if (!overriddenSymbol.isAbstract()
      && !isObjectMethod(overriddenSymbol)
      && !isAnnotatedOverride(methodSymbol)) {
      QuickFixHelper.newIssue(context)
        .forRule(this)
        .onTree(methodTree.simpleName())
        .withMessage("Add the \"@Override\" annotation above this method signature")
        .withQuickFix(() -> quickFix(methodTree))
        .report();
    }
  }

  private static boolean isExcludedByVersion(JavaVersion javaVersion) {
    if (javaVersion.isNotSet()) {
      return false;
    }
    return javaVersion.asInt() <= 4;
  }

  private static boolean isObjectMethod(Symbol.MethodSymbol method) {
    return method.owner().type().is("java.lang.Object");
  }

  private static boolean isAnnotatedOverride(Symbol.MethodSymbol method) {
    return method.metadata().isAnnotatedWith("java.lang.Override");
  }

  /**
   * Place the @Override annotation as first annotation, on top of the signature
   * @param methodTree the method to annotate
   * @return the quick-fix adding the @Override annotation one line above the signature
   */
  private JavaQuickFix quickFix(MethodTree methodTree) {
    ModifiersTree modifiersTree = methodTree.modifiers();
    Tree targetTree = modifiersTree.isEmpty() ? QuickFixHelper.nextToken(modifiersTree) : modifiersTree.get(0);
    String insertedText;
    if (somethingBeforeOnSameLine(methodTree)) {
      // strangely formated code: everythign on the same line?
      insertedText = "@Override ";
    } else {
      insertedText = "@Override" + newLineWithPadding(targetTree);
    }
    return JavaQuickFix
      .newQuickFix("Add \"@Override\" annotation")
      .addTextEdit(JavaTextEdit.insertBeforeTree(targetTree, insertedText))
      .build();
  }

  private static boolean somethingBeforeOnSameLine(Tree tree) {
    return QuickFixHelper.previousToken(tree).line() == tree.firstToken().line();
  }

  private String newLineWithPadding(Tree tree) {
    String endOfLineCharacters = endOfLineCharacters(tree);

    Position firstTokenStart = tree.firstToken().range().start();
    String padding = context.getFileLines()
      .get(firstTokenStart.lineOffset())
      .substring(0, firstTokenStart.columnOffset());

    return endOfLineCharacters + padding;
  }

  private String endOfLineCharacters(Tree tree) {
    String treeLine = QuickFixHelper.internalContext(context).getFileLinesWithLineEndings().get(tree.firstToken().line() - 1);
    StringBuilder sb = new StringBuilder();
    for (int i = treeLine.length() - 1; i >= 0; i--) {
      char character = treeLine.charAt(i);
      if ((character != '\r') && (character != '\n')) {
        break;
      }
      sb.insert(0, character);
    }

    return sb.toString();
  }
}
