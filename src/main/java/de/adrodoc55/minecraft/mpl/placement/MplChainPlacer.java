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
package de.adrodoc55.minecraft.mpl.placement;

import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;
import static de.kussm.direction.Direction.EAST;
import static de.kussm.direction.Direction.NORTH;
import static de.kussm.direction.Direction.WEST;
import static de.kussm.direction.Directions.$;
import static java.util.Comparator.naturalOrder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import de.adrodoc55.minecraft.coordinate.Axis3D;
import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Direction3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.blocks.AirBlock;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.blocks.Transmitter;
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.NoOperationCommand;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.kussm.ChainLayouter;
import de.kussm.chain.Chain;
import de.kussm.chain.ChainLinkType;
import de.kussm.direction.Directions;
import de.kussm.position.Position;

/**
 * @author Adrodoc55
 */
public abstract class MplChainPlacer {
  protected final ChainContainer container;
  protected final CompilerOptions options;
  protected final List<CommandBlockChain> chains = new ArrayList<>();

  protected MplChainPlacer(ChainContainer container, CompilerOptions options) {
    this.container = container;
    this.options = options;
  }

  protected Orientation3D getOrientation() {
    return container.getOrientation();
  }

  protected CommandChain getInstall() {
    return container.getInstall();
  }

  protected CommandChain getUninstall() {
    return container.getUninstall();
  }

  public abstract List<CommandBlockChain> place() throws NotEnoughSpaceException;

  /**
   * Generates a flat {@link CommandBlockChain}. Flat means, that the chain will not have any width
   * in the c direction of the orientation.<br>
   * The chain will not have any illegal transmitter or receiver regarding all chains that have
   * already been added to {@link #chains}. Also the chain will not have any illegally placed
   * conditional command blocks.
   *
   * @param chain
   * @param start the starting coordinate of the chain
   * @param template
   * @return
   * @throws NotEnoughSpaceException
   */
  public CommandBlockChain generateFlat(CommandChain chain, Coordinate3D start, Directions template)
      throws NotEnoughSpaceException {
    LinkedHashMap<Position, ChainLinkType> placed = place(chain, start, template);
    List<MplBlock> blocks = toBlocks(chain.getCommands(), placed);
    CommandBlockChain result = new CommandBlockChain(chain.getName(), blocks);
    result.move(start);
    return result;
  }

  /**
   * Places the given chain. The placement will not have any illegal transmitter or receiver
   * regarding all chains that have already been added to {@link #chains}. Also the chain will not
   * have any illegally placed conditional command blocks.
   *
   * @param chain
   * @param start
   * @param template
   * @return
   * @throws NotEnoughSpaceException
   */
  public LinkedHashMap<Position, ChainLinkType> place(CommandChain chain, Coordinate3D start,
      Directions template) throws NotEnoughSpaceException {
    Chain chainLinkChain = toChainLinkChain(chain.getCommands());
    Set<Position> forbiddenReceiver = new HashSet<>();
    Set<Position> forbiddenTransmitter = new HashSet<>();
    fillForbiddenPositions(start, forbiddenReceiver, forbiddenTransmitter);
    return place(chainLinkChain, template, forbiddenReceiver, forbiddenTransmitter);
  }

  private void fillForbiddenPositions(Coordinate3D start, Set<Position> forbiddenReceiver,
      Set<Position> forbiddenTransmitter) {
    Orientation3D orientation = getOrientation();
    Axis3D cAxis = orientation.getC().getAxis();
    int startC = start.get(cAxis);
    for (CommandBlockChain materialized : chains) {
      for (MplBlock block : materialized.getBlocks()) {
        Coordinate3D currentCoord = block.getCoordinate();
        int currentC = currentCoord.get(cAxis);

        Position pos = toPosition(currentCoord.minus(start), orientation);
        ImmutableSet<Position> illegalPositions;
        if (startC - 1 == currentC || currentC == startC + 1) {
          illegalPositions = ImmutableSet.<Position>of(pos);
        } else if (startC == currentC) {
          illegalPositions = pos.neighbours();
        } else {
          continue;
        }
        for (Position illegalPos : illegalPositions) {
          // Only look at positive positions
          if (illegalPos.getX() >= 0 && illegalPos.getY() >= 0) {
            if (isTransmitter(block)) {
              forbiddenReceiver.add(illegalPos);
            } else if (isReceiver(block)) {
              forbiddenTransmitter.add(illegalPos);
            }
          }
        }
      }
    }
  }

  /**
   * Places the chain according to the template without regarding forbidden transmitter/receiver
   * positions. The chain will not have any illegally placed conditional command blocks.
   *
   * @param chain
   * @param template
   * @return
   * @throws NotEnoughSpaceException
   */
  public LinkedHashMap<Position, ChainLinkType> place(Chain chain, Directions template)
      throws NotEnoughSpaceException {
    return place(chain, template, new HashSet<>(), new HashSet<>());
  }

  /**
   * Places the given chain. The placement will not have any illegal transmitter or receiver
   * regarding the parameters. Also the chain will not have any illegally placed conditional command
   * blocks.
   *
   * @param chain
   * @param template
   * @param forbiddenReceivers
   * @param forbiddenTransmitters
   * @return
   * @throws NotEnoughSpaceException
   */
  protected LinkedHashMap<Position, ChainLinkType> place(Chain chain, Directions template,
      Set<Position> forbiddenReceivers, Set<Position> forbiddenTransmitters)
          throws NotEnoughSpaceException {
    if (options.hasOption(TRANSMITTER)) {
      // receivers are not allowed at x=0 because the start transmitters of all chains are at x=0
      Predicate<Position> isReceiverAllowed =
          pos -> !forbiddenReceivers.contains(pos) && pos.getX() != 0;

      // transmitters are not allowed at x=1 because the start receivers of all chains are at x=1
      Predicate<Position> isTransmitterAllowed =
          pos -> !forbiddenTransmitters.contains(pos) && pos.getX() != 1;

      return ChainLayouter.place(chain, template, isReceiverAllowed, isTransmitterAllowed);
    } else {
      return ChainLayouter.place(chain, template);
    }
  }

  protected CommandChain getPopulatedInstall() {
    List<ChainLink> commands = getInstall().getCommands();
    ArrayList<ChainLink> result = new ArrayList<>(commands.size() + chains.size());
    result.addAll(commands);
    for (CommandBlockChain chain : chains) {
      String name = chain.getName();
      if (name == null || name == "install" || name == "uninstall") {
        continue;
      }
      Coordinate3D chainStart = chain.getBlocks().get(0).getCoordinate();
      int index = options.hasOption(TRANSMITTER) ? 2 : 1;
      // TODO: Alle ArmorStands taggen, damit nur ein uninstallation command notwendig
      result
          .add(index,
              new Command("/summon ArmorStand ${origin + (" + chainStart.toAbsoluteString()
                  + ")} {CustomName:" + name
                  + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}"));
    }
    return new CommandChain(getInstall().getName(), result);
  }

  protected CommandChain getPopulatedUninstall() {
    List<ChainLink> commands = getUninstall().getCommands();
    ArrayList<ChainLink> result = new ArrayList<>(commands.size() + chains.size());
    result.addAll(commands);
    for (CommandBlockChain chain : chains) {
      String name = chain.getName();
      if (name == null || name == "install" || name == "uninstall") {
        continue;
      }
      result.add(new Command("/kill @e[type=ArmorStand,name=" + name + "]"));
      // result.add(0, new Command("/kill @e[type=ArmorStand,name=" + name + "_NOTIFY]"));
      // result.add(0, new Command("/kill @e[type=ArmorStand,name=" + name + "_INTERCEPTED]"));
    }
    return new CommandChain(getUninstall().getName(), result);
  }

  protected List<MplBlock> toBlocks(List<ChainLink> commands,
      LinkedHashMap<Position, ChainLinkType> placed) {
    LinkedList<ChainLink> chainLinks = new LinkedList<>(commands);

    Orientation3D orientation = getOrientation();

    LinkedList<Entry<Position, ChainLinkType>> entries =
        placed.entrySet().stream().collect(Collectors.toCollection(LinkedList::new));

    List<MplBlock> blocks = new LinkedList<>();
    while (entries.size() > 1) {
      Entry<Position, ChainLinkType> entry = entries.pop();

      Position pos = entry.getKey();
      Position nextPos = entries.peek().getKey();

      Direction3D d = getDirection(pos, nextPos, orientation);
      Coordinate3D coord = toCoordinate(pos, orientation);

      if (entry.getValue() == ChainLinkType.NO_OPERATION) {
        blocks.add(new CommandBlock(new NoOperationCommand(), d, coord));
      } else {
        ChainLink chainLink = chainLinks.pop();
        if (chainLink instanceof Command) {
          blocks.add(new CommandBlock((Command) chainLink, d, coord));
        } else if (chainLink instanceof MplSkip) {
          blocks.add(new Transmitter(((MplSkip) chainLink).isInternal(), coord));
        }
      }
    }

    // last block is always air
    Position lastPos = entries.pop().getKey();
    blocks.add(new AirBlock(toCoordinate(lastPos, orientation)));
    return blocks;
  }

  public int getLongestSuccessiveConditionalCount() {
    return Stream
        .concat(container.getChains().stream().map(c -> c.getCommands()),
            Stream.of(getInstall().getCommands(), getUninstall().getCommands()))
        .map(commands -> getLongestSuccessiveConditionalCount(commands)).max(naturalOrder())
        .orElse(0);
  }

  public static int getLongestSuccessiveConditionalCount(List<? extends ChainLink> chainLinks) {
    Preconditions.checkNotNull(chainLinks, "chainLinks == null!");
    int result = 0;
    int successiveConditionalCount = 0;
    for (ChainLink chainLink : chainLinks) {
      if (chainLink instanceof Command && ((Command) chainLink).isConditional()) {
        successiveConditionalCount++;
      } else {
        result = Math.max(result, successiveConditionalCount);
        successiveConditionalCount = 0;
      }
    }
    result = Math.max(result, successiveConditionalCount);
    return result;
  }

  /**
   * x -> a, y -> b
   */
  public static Coordinate3D toCoordinate(Position pos, Orientation3D orientation) {
    Coordinate3D xDir = orientation.getA().toCoordinate();
    Coordinate3D yDir = orientation.getB().toCoordinate();
    Coordinate3D coord = xDir.mult(pos.getX()).plus(yDir.mult(pos.getY()));
    return coord;
  }

  /**
   * a -> x, b -> y
   */
  public static Position toPosition(Coordinate3D coord, Orientation3D orientation) {
    Direction3D a = orientation.getA();
    Direction3D b = orientation.getB();
    int x = coord.get(a.getAxis());
    int y = coord.get(b.getAxis());
    return Position.at(x, y);
  }

  /**
   *
   * @param cp current position
   * @param np next position
   * @param orientation
   * @return
   */
  protected static Direction3D getDirection(Position cp, Position np, Orientation3D orientation) {
    // current coordinate
    Coordinate3D cc = toCoordinate(cp, orientation);
    // next coordinate
    Coordinate3D nc = toCoordinate(np, orientation);
    return Direction3D.valueOf(nc.minus(cc));
  }

  public static boolean isTransmitter(MplBlock block) {
    return block instanceof Transmitter;
  }

  public static boolean isTransmitter(ChainLink chainLink) {
    return chainLink instanceof MplSkip;
  }

  public static boolean isReceiver(MplBlock block) {
    if (block instanceof CommandBlock) {
      CommandBlock commandBlock = (CommandBlock) block;
      return isReceiver(commandBlock.toCommand());
    } else {
      return false;
    }
  }

  public static boolean isReceiver(ChainLink chainLink) {
    if (chainLink instanceof Command) {
      Command command = (Command) chainLink;
      return command.getMode() != Mode.CHAIN;
    } else {
      return false;
    }
  }

  public static ChainLinkType getType(ChainLink chainLink) {
    if (isTransmitter(chainLink)) {
      return ChainLinkType.TRANSMITTER;
    } else if (isReceiver(chainLink)) {
      return ChainLinkType.RECEIVER;
    } else if (chainLink instanceof Command) {
      Command command = (Command) chainLink;
      if (command.isConditional()) {
        return ChainLinkType.CONDITIONAL;
      }
    }
    return ChainLinkType.NORMAL;
  }

  protected static Chain toChainLinkChain(List<ChainLink> chainLinks) {
    ArrayList<ChainLinkType> chainLinkTypes = new ArrayList<ChainLinkType>(chainLinks.size());
    for (ChainLink chainLink : chainLinks) {
      chainLinkTypes.add(getType(chainLink));
    }
    // add 1 normal ChainLink to the end (1 block air must be at the end in order to prevent chain
    // looping)
    chainLinkTypes.add(ChainLinkType.NORMAL);
    return Chain.of(chainLinkTypes.toArray(new ChainLinkType[0]));
  }

  protected static Directions newTemplate(int a) {
    return $(EAST.repeat(a - 1), NORTH, WEST.repeat(a - 1), NORTH).repeat();
  }

}
