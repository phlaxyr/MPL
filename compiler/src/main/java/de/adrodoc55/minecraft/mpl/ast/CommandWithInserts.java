/*
 * Minecraft Programming Language (MPL): A language for easy development of command block
 * applications including an IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * This file is part of MPL.
 *
 * MPL is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MPL is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MPL. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 *
 *
 * Minecraft Programming Language (MPL): Eine Sprache für die einfache Entwicklung von Commandoblock
 * Anwendungen, inklusive einer IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * Diese Datei ist Teil von MPL.
 *
 * MPL ist freie Software: Sie können diese unter den Bedingungen der GNU General Public License,
 * wie von der Free Software Foundation, Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * MPL wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG,
 * bereitgestellt; sogar ohne die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN
 * BESTIMMTEN ZWECK. Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit MPL erhalten haben. Wenn
 * nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.adrodoc55.minecraft.mpl.ast;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

import de.adrodoc55.commons.CopyScope;
import de.adrodoc55.commons.CopyScope.Copyable;
import de.adrodoc55.minecraft.mpl.MplUtils;
import de.adrodoc55.minecraft.mpl.interpretation.VariableScope;
import de.adrodoc55.minecraft.mpl.interpretation.insert.Insert;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Adrodoc55
 */
@EqualsAndHashCode
@ToString
public class CommandWithInserts implements Copyable {
  private final List<Object> commandParts = new ArrayList<>();
  private final List<Insert> inserts = new ArrayList<>();

  public CommandWithInserts() {}

  public CommandWithInserts(String command) {
    add(MplUtils.commandWithoutLeadingSlash(command));
  }

  @Deprecated
  protected CommandWithInserts(CommandWithInserts original) {}

  @Deprecated
  @Override
  public Copyable createFlatCopy(CopyScope scope) throws NullPointerException {
    return new CommandWithInserts(this);
  }

  @Deprecated
  @Override
  public void completeDeepCopy(CopyScope scope) throws NullPointerException {
    CommandWithInserts original = scope.getCache().getOriginal(this);
    commandParts.addAll(scope.copyObjects(original.commandParts));
    inserts.addAll(scope.copyObjects(original.inserts));
  }

  public void add(String string) {
    commandParts.add(string);
  }

  public void add(Insert insert) {
    commandParts.add(insert);
    inserts.add(insert);
  }

  public String getCommand() {
    return Joiner.on("").join(commandParts);
  }

  public void resolve(VariableScope scope) {
    for (Insert insert : inserts) {
      insert.resolve(scope);
    }
  }
}