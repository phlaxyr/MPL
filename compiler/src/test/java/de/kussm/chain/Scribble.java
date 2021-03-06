package de.kussm.chain;

import static de.kussm.chain.ChainLinkType.CONDITIONAL;
import static de.kussm.chain.ChainLinkType.NORMAL;
import static de.kussm.chain.ChainLinkType.RECEIVER;
import static de.kussm.chain.ChainLinkType.TRANSMITTER;
import static de.kussm.direction.Direction.EAST;
import static de.kussm.direction.Direction.NORTH;
import static de.kussm.direction.Direction.WEST;
import static de.kussm.direction.Directions.$;

import de.adrodoc55.minecraft.mpl.placement.NotEnoughSpaceException;
import de.kussm.direction.Directions;

/**
 * @author Michael Kuß
 */
public class Scribble {
  private static final Chain EXAMPLE_CHAIN1 = Chain.of(TRANSMITTER, RECEIVER, NORMAL, NORMAL,
      NORMAL, TRANSMITTER, RECEIVER, NORMAL, CONDITIONAL, CONDITIONAL, CONDITIONAL, CONDITIONAL,
      NORMAL, CONDITIONAL, TRANSMITTER, RECEIVER, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
      NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
      NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
      NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
      NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
      NORMAL, NORMAL, NORMAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, CONDITIONAL, NORMAL,
      CONDITIONAL, NORMAL, CONDITIONAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL,
      CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, CONDITIONAL, NORMAL, NORMAL, NORMAL,
      CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL,
      CONDITIONAL, CONDITIONAL, CONDITIONAL, NORMAL, CONDITIONAL, CONDITIONAL, NORMAL, CONDITIONAL,
      CONDITIONAL, CONDITIONAL, NORMAL, CONDITIONAL, CONDITIONAL, NORMAL, NORMAL, CONDITIONAL,
      NORMAL, CONDITIONAL, NORMAL);

  private static final Chain EXAMPLE_CHAIN2 = Chain.of(TRANSMITTER, RECEIVER, NORMAL, NORMAL,
      NORMAL, TRANSMITTER, RECEIVER, NORMAL, NORMAL, NORMAL, CONDITIONAL, CONDITIONAL, CONDITIONAL,
      NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
      NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL,
      NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL,
      CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL,
      CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, NORMAL, CONDITIONAL, NORMAL,
      CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL,
      CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL,
      CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL,
      CONDITIONAL, NORMAL, CONDITIONAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL,
      NORMAL, CONDITIONAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, CONDITIONAL,
      NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL,
      CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
      NORMAL, CONDITIONAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
      NORMAL);

  private static final Chain EXAMPLE_CHAIN3 = Chain.of(TRANSMITTER, RECEIVER, NORMAL, NORMAL,
      CONDITIONAL, CONDITIONAL, CONDITIONAL, CONDITIONAL, CONDITIONAL, CONDITIONAL, NORMAL, NORMAL,
      NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
      NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
      NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL);


  public static void main(String[] args) throws NotEnoughSpaceException {
    // Directions dirs = $(EAST.repeat(16), NORTH, WEST.repeat(16), NORTH).repeat(1000);
    Directions dirs = $(EAST.repeat(4), NORTH, WEST.repeat(4), NORTH).repeat();
    System.out.println(ChainLayouter.place(EXAMPLE_CHAIN1, dirs));
    System.out.println(ChainLayouter.place(EXAMPLE_CHAIN2, dirs));
    System.out.println(ChainLayouter.place(EXAMPLE_CHAIN3, dirs));
  }
}
