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
package de.adrodoc55.minecraft.mpl.interpretation;

import static de.adrodoc55.minecraft.mpl.MplUtils.commandWithoutLeadingSlash;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.adrodoc55.minecraft.mpl.interpretation.insert.GlobalVariableInsert;
import de.adrodoc55.minecraft.mpl.interpretation.insert.RelativeOriginInsert;
import de.adrodoc55.minecraft.mpl.interpretation.insert.RelativeThisInsert;
import de.adrodoc55.minecraft.mpl.interpretation.insert.TargetedThisInsert;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.karneim.pojobuilder.GenerateMplPojoBuilder;

/**
 * @author Adrodoc55
 */
@EqualsAndHashCode
@ToString
public class CommandPartBuffer {
  private final List<Object> commandParts = new ArrayList<>();
  private final List<RelativeThisInsert> thisInserts = new ArrayList<>();
  private final List<TargetedThisInsert> targetedThisInserts = new ArrayList<>();
  private final List<RelativeOriginInsert> originInserts = new ArrayList<>();
  private final List<GlobalVariableInsert> variableInserts = new ArrayList<>();

  public CommandPartBuffer() {}

  @GenerateMplPojoBuilder
  public CommandPartBuffer(String command) {
    add(commandWithoutLeadingSlash(command));
  }

  public List<Object> getCommandParts() {
    return Collections.unmodifiableList(commandParts);
  }

  public void add(String string) {
    commandParts.add(string);
  }

  public CommandPartBuffer with(String string) {
    add(string);
    return this;
  }

  public Collection<RelativeThisInsert> getThisInserts() {
    return Collections.unmodifiableList(thisInserts);
  }

  public void add(RelativeThisInsert insert) {
    commandParts.add(insert);
    thisInserts.add(insert);
  }

  public CommandPartBuffer with(RelativeThisInsert insert) {
    add(insert);
    return this;
  }

  public Collection<TargetedThisInsert> getTargetedThisInserts() {
    return Collections.unmodifiableList(targetedThisInserts);
  }

  public void add(TargetedThisInsert insert) {
    commandParts.add(insert);
    targetedThisInserts.add(insert);
  }

  public CommandPartBuffer with(TargetedThisInsert insert) {
    add(insert);
    return this;
  }

  public Collection<RelativeOriginInsert> getOriginInserts() {
    return Collections.unmodifiableList(originInserts);
  }

  public void add(RelativeOriginInsert insert) {
    commandParts.add(insert);
    originInserts.add(insert);
  }

  public CommandPartBuffer with(RelativeOriginInsert insert) {
    add(insert);
    return this;
  }

  public Collection<GlobalVariableInsert> getVariableInserts() {
    return Collections.unmodifiableList(variableInserts);
  }

  public void add(GlobalVariableInsert insert) {
    commandParts.add(insert);
    variableInserts.add(insert);
  }

  public CommandPartBuffer with(GlobalVariableInsert insert) {
    add(insert);
    return this;
  }

  public interface TargetedThisInsertResolver {
    RelativeThisInsert resolve(TargetedThisInsert insert) throws UnableToResolveInsertException;
  }

  public void resolveTargetedThisInserts(TargetedThisInsertResolver resolver) {
    Iterator<TargetedThisInsert> it = targetedThisInserts.iterator();
    while (it.hasNext()) {
      try {
        TargetedThisInsert insert = it.next();
        RelativeThisInsert result = resolver.resolve(insert);
        thisInserts.add(result);
        commandParts.replaceAll(o -> o == insert ? result : o);
        it.remove();
      } catch (UnableToResolveInsertException ex) {
        // Ignore inserts that can't be resolved yet.
      }
    }
  }
}
