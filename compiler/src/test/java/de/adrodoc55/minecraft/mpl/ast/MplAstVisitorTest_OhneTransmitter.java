/*
 * MPL (Minecraft Programming Language): A language for easy development of commandblock
 * applications including and IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * This file is part of MPL (Minecraft Programming Language).
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
 * MPL (Minecraft Programming Language): Eine Sprache für die einfache Entwicklung von Commandoblock
 * Anwendungen, beinhaltet eine IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * Diese Datei ist Teil von MPL (Minecraft Programming Language).
 *
 * MPL ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie
 * von der Free Software Foundation, Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
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

import static de.adrodoc55.TestBase.$Enum;
import static de.adrodoc55.TestBase.listOf;
import static de.adrodoc55.TestBase.some;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplBreakpoint;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplCommand;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplIf;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplIntercept;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplProcess;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplWaitfor;
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept.INTERCEPTED;
import static de.adrodoc55.minecraft.mpl.commands.Conditional.CONDITIONAL;
import static de.adrodoc55.minecraft.mpl.commands.Conditional.INVERT;
import static de.adrodoc55.minecraft.mpl.commands.Conditional.UNCONDITIONAL;
import static de.adrodoc55.minecraft.mpl.commands.Mode.CHAIN;
import static de.adrodoc55.minecraft.mpl.commands.Mode.IMPULSE;
import static de.adrodoc55.minecraft.mpl.commands.Mode.REPEAT;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DEBUG;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import de.adrodoc55.minecraft.mpl.ast.chainparts.Dependable;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplBreakpoint;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIf;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplWaitfor;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InternalCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InvertingCommand;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;

public class MplAstVisitorTest_OhneTransmitter extends MplAstVisitorTest {

  @Override
  protected MplAstVisitorImpl newUnderTest() {
    return new MplAstVisitorImpl(new CompilerOptions(DEBUG));
  }

  @Override
  protected String getOnCommand(String ref) {
    return "blockdata " + ref + " {auto:1}";
  }

  @Override
  protected String getOffCommand(String ref) {
    return "blockdata " + ref + " {auto:0}";
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //    ___                          _
  //   |_ _| _ __ __   __ ___  _ __ | |_
  //    | | | '_ \\ \ / // _ \| '__|| __|
  //    | | | | | |\ V /|  __/| |   | |_
  //   |___||_| |_| \_/  \___||_|    \__|
  //
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Test
  public void test_invert_modifier_referenziert_den_richtigen_mode() {
    // given:
    MplCommand first = some($MplCommand()//
        .withConditional(UNCONDITIONAL));

    MplCommand second = some($MplCommand()//
        .withConditional(INVERT)//
        .withPrevious(first));

    MplProcess process = some($MplProcess()//
        .withRepeating(false)//
        .withChainParts(listOf(first, second)));

    // when:
    process.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(getOffCommand("~ ~ ~"), IMPULSE), //
        new Command(first.getCommand(), first), //
        new InvertingCommand(first.getMode()), // Important line!
        new Command(second.getCommand(), second));
  }

  @Test
  public void test_Der_erste_invert_in_einem_repeating_process_referenziert_einen_repeating_command_block() {
    // given:
    MplCommand first = some($MplCommand()//
        .withConditional(UNCONDITIONAL));

    MplCommand second = some($MplCommand()//
        .withConditional(INVERT)//
        .withPrevious(first));

    MplProcess process = some($MplProcess()//
        .withRepeating(true)//
        .withChainParts(listOf(first, second)));

    // when:
    process.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new Command(first.getCommand(), REPEAT, false, first.getNeedsRedstone()), //
        new InvertingCommand(REPEAT), // Important line!
        new Command(second.getCommand(), second));
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //   __        __      _  _     __
  //   \ \      / /__ _ (_)| |_  / _|  ___   _ __
  //    \ \ /\ / // _` || || __|| |_  / _ \ | '__|
  //     \ V  V /| (_| || || |_ |  _|| (_) || |
  //      \_/\_/  \__,_||_| \__||_|   \___/ |_|
  //
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Test
  public void test_unconditional_Waitfor() {
    // given:
    MplWaitfor mplWaitfor = some($MplWaitfor()//
        .withConditional(UNCONDITIONAL));

    // when:
    mplWaitfor.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand("/summon ArmorStand ${this + 1} {CustomName:" + mplWaitfor.getEvent()
            + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}"), //
        new InternalCommand(getOffCommand("~ ~ ~"), IMPULSE));
  }

  @Test
  public void test_conditional_Waitfor() {
    // given:
    MplWaitfor mplWaitfor = some($MplWaitfor()//
        .withConditional(CONDITIONAL));

    // when:
    mplWaitfor.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand("/summon ArmorStand ${this + 3} {CustomName:" + mplWaitfor.getEvent()
            + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}", true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + 1}"), true), //
        new InternalCommand(getOffCommand("~ ~ ~"), IMPULSE));
  }

  @Test
  public void test_invert_Waitfor() {
    // given:
    MplWaitfor mplWaitfor = some($MplWaitfor()//
        .withConditional(INVERT));

    // when:
    mplWaitfor.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(getOnCommand("${this + 3}"), true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand("/summon ArmorStand ${this + 1} {CustomName:" + mplWaitfor.getEvent()
            + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}", true), //
        new InternalCommand(getOffCommand("~ ~ ~"), IMPULSE));
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //    ___         _                                _
  //   |_ _| _ __  | |_  ___  _ __  ___  ___  _ __  | |_
  //    | | | '_ \ | __|/ _ \| '__|/ __|/ _ \| '_ \ | __|
  //    | | | | | || |_|  __/| |  | (__|  __/| |_) || |_
  //   |___||_| |_| \__|\___||_|   \___|\___|| .__/  \__|
  //                                         |_|
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Test
  public void test_unconditional_Intercept() {
    // given:
    MplIntercept mplIntercept = some($MplIntercept()//
        .withConditional(UNCONDITIONAL));

    // when:
    mplIntercept.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand("/entitydata @e[name=" + mplIntercept.getEvent() + "] {CustomName:"
            + mplIntercept.getEvent() + INTERCEPTED + "}"), //
        new InternalCommand("/summon ArmorStand ${this + 1} {CustomName:" + mplIntercept.getEvent()
            + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}"), //
        new InternalCommand(getOffCommand("~ ~ ~"), IMPULSE), //
        new InternalCommand("/kill @e[name=" + mplIntercept.getEvent() + ",r=2]"), //
        new InternalCommand("/entitydata @e[name=" + mplIntercept.getEvent() + INTERCEPTED
            + "] {CustomName:" + mplIntercept.getEvent() + "}"));
  }

  @Test
  public void test_conditional_Intercept() {
    // given:
    Mode mode = some($Enum(Mode.class));
    MplIntercept mplIntercept = some($MplIntercept()//
        .withConditional(CONDITIONAL)//
        .withPrevious(new Dependable() {
          @Override
          public boolean canBeDependedOn() {
            return true;
          }

          @Override
          public Mode getModeForInverting() throws UnsupportedOperationException {
            return mode;
          }
        }));

    // when:
    mplIntercept.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand("/entitydata @e[name=" + mplIntercept.getEvent() + "] {CustomName:"
            + mplIntercept.getEvent() + INTERCEPTED + "}", true), //
        new InternalCommand("/summon ArmorStand ${this + 3} {CustomName:" + mplIntercept.getEvent()
            + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}", true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + 1}"), true), //
        new InternalCommand(getOffCommand("~ ~ ~"), IMPULSE), //
        new InternalCommand("/kill @e[name=" + mplIntercept.getEvent() + ",r=2]"), //
        new InternalCommand("/entitydata @e[name=" + mplIntercept.getEvent() + INTERCEPTED
            + "] {CustomName:" + mplIntercept.getEvent() + "}"));
  }

  @Test
  public void test_invert_Intercept() {
    // given:
    Mode mode = some($Enum(Mode.class));
    MplIntercept mplIntercept = some($MplIntercept()//
        .withConditional(INVERT)//
        .withPrevious(new Dependable() {
          @Override
          public boolean canBeDependedOn() {
            return true;
          }

          @Override
          public Mode getModeForInverting() throws UnsupportedOperationException {
            return mode;
          }
        }));

    // when:
    mplIntercept.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(getOnCommand("${this + 4}"), true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand("/entitydata @e[name=" + mplIntercept.getEvent() + "] {CustomName:"
            + mplIntercept.getEvent() + INTERCEPTED + "}", true), //
        new InternalCommand("/summon ArmorStand ${this + 1} {CustomName:" + mplIntercept.getEvent()
            + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}", true), //
        new InternalCommand(getOffCommand("~ ~ ~"), IMPULSE), //
        new InternalCommand("/kill @e[name=" + mplIntercept.getEvent() + ",r=2]"), //
        new InternalCommand("/entitydata @e[name=" + mplIntercept.getEvent() + INTERCEPTED
            + "] {CustomName:" + mplIntercept.getEvent() + "}"));
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //    ____                     _                   _         _
  //   | __ )  _ __  ___   __ _ | | __ _ __    ___  (_) _ __  | |_
  //   |  _ \ | '__|/ _ \ / _` || |/ /| '_ \  / _ \ | || '_ \ | __|
  //   | |_) || |  |  __/| (_| ||   < | |_) || (_) || || | | || |_
  //   |____/ |_|   \___| \__,_||_|\_\| .__/  \___/ |_||_| |_| \__|
  //                                  |_|
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Test
  public void test_unconditional_Breakpoint() {
    // given:
    MplBreakpoint mplBreakpoint = some($MplBreakpoint()//
        .withConditional(UNCONDITIONAL));

    // when:
    mplBreakpoint.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand("/say " + mplBreakpoint.getMessage()), //
        new InternalCommand("/execute @e[name=breakpoint] ~ ~ ~ " + getOnCommand("~ ~ ~")), //
        new InternalCommand(
            "/summon ArmorStand ${this + 1} {CustomName:breakpoint_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}"), //
        new InternalCommand(getOffCommand("~ ~ ~"), IMPULSE));
  }

  @Test
  public void test_conditional_Breakpoint() {
    // given:
    MplBreakpoint mplBreakpoint = some($MplBreakpoint()//
        .withConditional(CONDITIONAL));

    // when:
    mplBreakpoint.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand("/say " + mplBreakpoint.getMessage(), true), //
        new InternalCommand("/execute @e[name=breakpoint] ~ ~ ~ " + getOnCommand("~ ~ ~"), true), //
        new InternalCommand(
            "/summon ArmorStand ${this + 3} {CustomName:breakpoint_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}",
            true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + 1}"), true), //
        new InternalCommand(getOffCommand("~ ~ ~"), IMPULSE));
  }

  @Test
  public void test_invert_Breakpoint() {
    // given:
    Mode mode = some($Enum(Mode.class));
    MplBreakpoint mplBreakpoint = some($MplBreakpoint()//
        .withConditional(INVERT)//
        .withPrevious(new Dependable() {
          @Override
          public boolean canBeDependedOn() {
            return true;
          }

          @Override
          public Mode getModeForInverting() throws UnsupportedOperationException {
            return mode;
          }
        }));

    // when:
    mplBreakpoint.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InvertingCommand(mode), //
        new InternalCommand("/say " + mplBreakpoint.getMessage(), true), //
        new InternalCommand("/execute @e[name=breakpoint] ~ ~ ~ " + getOnCommand("~ ~ ~"), true), //
        new InternalCommand(
            "/summon ArmorStand ${this + 3} {CustomName:breakpoint_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}",
            true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + 1}"), true), //
        new InternalCommand(getOffCommand("~ ~ ~"), IMPULSE));
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //    ___   __             _____  _                              _____  _
  //   |_ _| / _|           |_   _|| |__    ___  _ __             | ____|| | ___   ___
  //    | | | |_              | |  | '_ \  / _ \| '_ \            |  _|  | |/ __| / _ \
  //    | | |  _|  _  _  _    | |  | | | ||  __/| | | |  _  _  _  | |___ | |\__ \|  __/
  //   |___||_|   (_)(_)(_)   |_|  |_| |_| \___||_| |_| (_)(_)(_) |_____||_||___/ \___|
  //
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Test
  public void test_commands_im_ersten_if_ohne_normalizer_in_einem_repeating_process_referenzieren_einen_repeating_command_block() {
    // given:
    MplCommand first = some($MplCommand()//
        .withConditional(UNCONDITIONAL));

    MplIf mplIf = some($MplIf()//
        .withNot(true)//
        .withThenParts(listOf(first)));

    MplProcess process = some($MplProcess()//
        .withRepeating(true)//
        .withChainParts(listOf(mplIf)));

    // when:
    process.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition(), REPEAT), //
        // then
        new InternalCommand(
            "/testforblock ${this - 1} repeating_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(first.getCommand(), first.getMode(), true, first.getNeedsRedstone())//
    );
  }

}