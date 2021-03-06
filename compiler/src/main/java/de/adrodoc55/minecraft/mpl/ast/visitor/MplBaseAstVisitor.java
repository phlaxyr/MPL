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
package de.adrodoc55.minecraft.mpl.ast.visitor;

import static com.google.common.base.Preconditions.checkState;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.CONDITIONAL;
import static de.adrodoc55.minecraft.mpl.commands.Mode.IMPULSE;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newCommand;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newInternalCommand;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newInvertingCommand;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;
import static de.adrodoc55.minecraft.mpl.interpretation.ModifierBuffer.modifier;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckReturnValue;

import de.adrodoc55.minecraft.mpl.MplUtils;
import de.adrodoc55.minecraft.mpl.ast.Conditional;
import de.adrodoc55.minecraft.mpl.ast.chainparts.Dependable;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ModifiableChainPart;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.interpretation.CommandPartBuffer;
import de.adrodoc55.minecraft.mpl.interpretation.insert.RelativeThisInsert;
import de.adrodoc55.minecraft.mpl.interpretation.insert.TargetedThisInsert;

/**
 * @author Adrodoc55
 */
public abstract class MplBaseAstVisitor implements MplAstVisitor<List<ChainLink>> {
  protected final CompilerOptions options;

  public MplBaseAstVisitor(CompilerOptions options) {
    this.options = options;
  }

  @CheckReturnValue
  private String getStartCommandHeader() {
    return MplUtils.getStartCommandHeader(options);
  }

  @CheckReturnValue
  private String getStartCommandTrailer() {
    return MplUtils.getStartCommandTrailer(options);
  }

  @CheckReturnValue
  protected String getStartCommand() {
    return MplUtils.getStartCommand(options);
  }

  @CheckReturnValue
  protected CommandPartBuffer getStartCommand(TargetedThisInsert insert) {
    CommandPartBuffer result = new CommandPartBuffer();
    result.add(getStartCommandHeader());
    result.add(insert);
    result.add(getStartCommandTrailer());
    return result;
  }

  @CheckReturnValue
  protected CommandPartBuffer getStartCommand(ChainLink target) {
    return getStartCommand(new TargetedThisInsert(target));
  }

  @CheckReturnValue
  private String getStopCommandHeader() {
    return MplUtils.getStopCommandHeader(options);
  }

  @CheckReturnValue
  private String getStopCommandTrailer() {
    return MplUtils.getStopCommandTrailer(options);
  }

  @CheckReturnValue
  protected String getStopCommand() {
    return MplUtils.getStopCommand(options);
  }

  @CheckReturnValue
  protected CommandPartBuffer getStopCommand(TargetedThisInsert insert) {
    CommandPartBuffer result = new CommandPartBuffer();
    result.add(getStopCommandHeader());
    result.add(insert);
    result.add(getStopCommandTrailer());
    return result;
  }

  @CheckReturnValue
  protected CommandPartBuffer getStopCommand(ChainLink target) {
    return getStopCommand(new TargetedThisInsert(target));
  }

  @CheckReturnValue
  protected CommandPartBuffer getStopCommand(int relative) {
    CommandPartBuffer result = new CommandPartBuffer();
    result.add(getStopCommandHeader());
    result.add(new RelativeThisInsert(relative));
    result.add(getStopCommandTrailer());
    return result;
  }

  @CheckReturnValue
  protected List<ChainLink> getRestartBackref(ChainLink target, boolean conditional) {
    List<ChainLink> result = new ArrayList<>(2);
    result.add(
        newInternalCommand(getStopCommand(target), modifier(Conditional.valueOf(conditional))));
    result.add(newInternalCommand(getStartCommand(target), modifier(CONDITIONAL)));
    return result;
  }

  @CheckReturnValue
  protected List<ChainLink> newJumpDestination(boolean internal) {
    if (options.hasOption(TRANSMITTER)) {
      List<ChainLink> result = new ArrayList<>(2);
      result.add(new MplSkip(internal));
      result.add(newInternalCommand(getStopCommand(-1), modifier(IMPULSE)));
      return result;
    } else {
      List<ChainLink> result = new ArrayList<>(1);
      if (internal) {
        result.add(newInternalCommand(getStopCommand(), modifier(IMPULSE)));
      } else {
        result.add(newCommand(getStopCommand(), modifier(IMPULSE)));
      }
      return result;
    }
  }

  /**
   * Checks if the given {@link ModifiableChainPart} has the {@link Conditional#INVERT INVERT}
   * modifier. If it does, an {@link Commands#newInvertingCommand inverting command} is added to
   * {@code commands}. If {@code chainPart} does not have predecessor an
   * {@link IllegalStateException} is thrown.
   *
   * @param commands the list to add to
   * @param chainPart the {@link ModifiableChainPart} to check
   * @throws IllegalStateException if {@code chainPart} does not have predecessor
   * @see ModifiableChainPart#getPrevious()
   */
  protected static void addInvertingCommandIfInvert(List<? super Command> commands,
      ModifiableChainPart chainPart) throws IllegalStateException {
    if (chainPart.getConditional() == Conditional.INVERT) {
      Dependable previous = chainPart.getPrevious();
      checkState(previous != null,
          "Cannot invert ChainPart; no previous command found for " + chainPart);
      commands.add(newInvertingCommand(previous));
    }
  }

  protected static void resolveReferences(List<ChainLink> chainLinks) {
    for (ChainLink chainLink : chainLinks) {
      chainLink.resolveTargetedThisInserts(chainLinks);
    }
  }
}
