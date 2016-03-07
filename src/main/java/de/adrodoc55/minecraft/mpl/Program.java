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
package de.adrodoc55.minecraft.mpl;

import java.util.LinkedList;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;

@Deprecated
public class Program {

  private LinkedList<CommandChain> chains;
  private LinkedList<ChainPart> installation;
  private LinkedList<ChainPart> uninstallation;

  // Compiler-Options
  private Coordinate3D max =
      new Coordinate3D(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
  private String prefix;
  private Orientation3D orientation;

  public Program() {
    chains = new LinkedList<CommandChain>();
    installation = new LinkedList<>();
    uninstallation = new LinkedList<>();
  }

  public LinkedList<CommandChain> getChains() {
    return chains;
  }

  public void setChains(LinkedList<CommandChain> chains) {
    this.chains = chains;
  }

  public LinkedList<ChainPart> getInstallation() {
    return installation;
  }

  public void setInstallation(LinkedList<ChainPart> installation) {
    this.installation = installation;
  }

  public LinkedList<ChainPart> getUninstallation() {
    return uninstallation;
  }

  public void setUninstallation(LinkedList<ChainPart> uninstallation) {
    this.uninstallation = uninstallation;
  }

  public Coordinate3D getMax() {
    return max;
  }

  public void setMax(Coordinate3D max) {
    this.max = max;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public Orientation3D getOrientation() {
    return orientation;
  }

  public void setOrientation(Orientation3D orientation) {
    this.orientation = orientation;
  }

}
