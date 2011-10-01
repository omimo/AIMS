package massim;

/*
Colored Trails

Copyright (C) 2006-2007, President and Fellows of Harvard College.  All Rights Reserved.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/


/**
<b>Description</b>
All game board locations are described as row-column pairs, which are 
represented by the RowCol class.  This class includes methods to determine 
whether two locations are the same ('equals()') and whether two locations 
are adjacent to each other ('areNeighbors()').

<p>

<b>Issues</b>
This class has two fields, representing a row and column on the board.  
These are public fields and they lack 'get' and 'set' methods.
<p>
[depending upon how RowCol instances are used, we might want to think 
about adding accessors and making the fields immutable (by making them 'final') 
once they are set in the constructor.]

<p>

<b>Future Development</b>
If we are interested to pursue other game board geometries, then more generic 
ways of representing location need to be constructed.  Nevertheless, from the 
point of view of an API, the 'equals' and 'areNeighbors' methods are already general.

<p>

<b>Original Summary</b>
* A type representing a (row,col) position, as opposed to (x,y).  All
* coordinates in CT3 should be in terms of (row,col).
*
* @author Paul Heymann (ct3@heymann.be)
@author Sevan G. Ficici (class-level review and comments)
*/


import java.io.Serializable;
import java.util.LinkedHashSet;


public class RowCol implements Serializable {
/** Rows */
public int row;
/** Columns */
public int col;

public RowCol(int row, int col) {
    this.row = row;
    this.col = col;
}

public RowCol(RowCol rc) {
    this.row = rc.row;
    this.col = rc.col;
}

/**
	Move constructor (SGF)
	
	@param rc			starting point
	@param deltarow		change in row (may be negative)
	@param deltacol		change in column (may be negative)
*/
public RowCol(RowCol rc, int deltarow, int deltacol)
{
	this.row = rc.row + deltarow;
	this.col = rc.col + deltacol;
}

public String toString() {
    return "(" + row + "," + col + ")";
}

/**
 * Determine if this position equals another.
 * @param other The other position to compare to.
 * @return Whether the two RowCol objects refer to the same position.
 */
public boolean equals(Object o) {
	RowCol other = (RowCol) o;
    return (row == other.row && col == other.col);
}

/**
    Calculate distance from here to some other location (SGF)
*/
public int dist(RowCol other)
{
    return Math.abs(this.row - other.row) + Math.abs(this.col - other.col);
}

/**
 * Determine whether two (row,col) positions are neighbors.
 * @param rc1 The first position.
 * @param rc2 The second position.
 * @return Whether the positions are in fact neighbors.
 */
public static boolean areNeighbors(RowCol rc1, RowCol rc2) {
    return ((Math.abs(rc1.row - rc2.row) +
            Math.abs(rc1.col - rc2.col)) == 1);
}

/**
 * Gets the neighbor position of a position
 * @param board Board of the game
 * @return Neighbor positions
 * @author ilke
 */
public LinkedHashSet<RowCol> getNeighbors(Board board) {
	LinkedHashSet<RowCol> neighbors = new LinkedHashSet<RowCol>();
	/*
	if( col-1 >= 0 )
		neighbors.add(new RowCol(row, col-1));
	if( row-1 >= 0 )
		neighbors.add(new RowCol(row-1, col));
	if( col + 1 < board.getCols())
		neighbors.add(new RowCol(row, col+1));
	if( row + 1 < board.getRows())
		neighbors.add(new RowCol(row+1, col));
	*/
	return neighbors;
}
}