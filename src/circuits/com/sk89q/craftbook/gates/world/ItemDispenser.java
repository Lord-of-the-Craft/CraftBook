// $Id$
/*
 * Copyright (C) 2010, 2011 sk89q <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.craftbook.gates.world;

import static com.sk89q.craftbook.ic.TripleInputChipState.input;
import static com.sk89q.craftbook.ic.TripleInputChipState.output;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import com.sk89q.craftbook.ic.AbstractIC;
import com.sk89q.craftbook.ic.AbstractICFactory;
import com.sk89q.craftbook.ic.ChipState;
import com.sk89q.craftbook.ic.IC;
import com.sk89q.worldedit.blocks.BlockType;

public class ItemDispenser extends AbstractIC {

	protected boolean risingEdge;

	public ItemDispenser(Server server, Sign sign, boolean risingEdge) {
		super(server, sign);
		this.risingEdge = risingEdge;
	}

	@Override
	public String getTitle() {
		return "Item Dispenser";
	}

	@Override
	public String getSignTitle() {
		return "ITEM DISPENSER";
	}

	@Override
	public void trigger(ChipState chip) {
		if (risingEdge && input(chip, 0) || (!risingEdge && !input(chip, 0))) {
			String item = getSign().getLine(2);
			int amount = 1;

			try {
				amount = Math.min(64,
						Math.max(-1, Integer.parseInt(getSign().getLine(2))));
			} catch (NumberFormatException e) {
			}
			String material = getSign().getLine(3);
			byte data = 0;
			if (material.contains(":")) {
				data = Byte.parseByte(material.split(":")[1]);
				material = material.split(":")[0];
			}
			Material mat = Material.matchMaterial(getSign().getLine(3));
			if (mat == null)
				return;
			int id = mat.getId();

			if (id != 0 && id != 36 && !(id >= 36 && id <= 34)) {
				Location loc = getSign().getBlock().getLocation();
				int maxY = Math.min(128, loc.getBlockY() + 10);
				int x = loc.getBlockX();
				int z = loc.getBlockZ();

				for (int y = loc.getBlockY() + 1; y <= maxY; y++) {
					if (BlockType.canPassThrough(getSign().getWorld()
							.getBlockTypeIdAt(x, y, z))) {
						getSign().getWorld().dropItemNaturally(
								new Location(getSign().getWorld(), x, y, z),
								new ItemStack(id, amount, data));
						return;
					}
				}
			}
		}
	}

	public static class Factory extends AbstractICFactory {

		protected boolean risingEdge;

		public Factory(Server server, boolean risingEdge) {
			super(server);
			this.risingEdge = risingEdge;
		}

		@Override
		public IC create(Sign sign) {
			return new ItemDispenser(getServer(), sign, risingEdge);
		}
	}

}
