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
package de.adrodoc55.minecraft.mpl.chain;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.adrodoc55.commons.Named;
import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.MplUtils;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import lombok.ToString;

/**
 * @author Adrodoc55
 */
@ToString
public class CommandBlockChain implements Named {
  private final String name;
  private final List<MplBlock> blocks = new ArrayList<>();
  private final List<String> tags = new ArrayList<>();

  public CommandBlockChain(String name, Collection<MplBlock> blocks, Collection<String> tags) {
    this.name = name;
    setBlocks(blocks);
    setTags(tags);
  }

  @Override
  public String getName() {
    return name;
  }

  public List<MplBlock> getBlocks() {
    return Collections.unmodifiableList(blocks);
  }

  public void setBlocks(Collection<MplBlock> blocks) {
    this.blocks.clear();
    this.blocks.addAll(blocks);
  }

  public List<String> getTags() {
    return Collections.unmodifiableList(tags);
  }

  public void setTags(Collection<String> tags) {
    this.tags.clear();
    this.tags.addAll(tags);
  }

  /**
   * Moves this Chain according to the given vector.
   *
   * @param vector to move this chain
   */
  public void move(Coordinate3D vector) {
    for (MplBlock block : blocks) {
      block.setCoordinate(block.getCoordinate().plus(vector));
    }
  }

  public Coordinate3D getBoundaries(Orientation3D orientation) {
    return MplUtils.getBoundaries(orientation,
        blocks.stream()//
            .map(b -> b.getCoordinate())//
            .collect(toList()));
  }

}
