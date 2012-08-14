package org.sblaj

import java.util

/**
 *
 */

trait SparseBinaryVector {
  //TODO can the dense matrix class also be generified, and still performant?

  def nnz : Int

  /**
   * return the value of the given column (either 0 or 1)
   * <p>
   * Note that though this method is provided for convenience, in general you should *NOT*
   * use it, because it will be slow
   * @param col
   * @return
   */
  def get(col: Int) : Int

  def dot(x:Array[Float]) : Float

  /**
   * compute (this)x(theta), and *add* the result to <code>into<code>
   * <p>
   * eg., take this as a row vector, and multiply it by the dense matrix
   * theta, and store the result in the given array
   *
   * @param theta a dense matrix
   * @param into store the result in this array
   */
  def preMult(theta:Array[Array[Float]], into: Array[Float]) : Unit
}


class BaseSparseBinaryVector (colIds: Array[Int], startIdx: Int, endIdx: Int)
extends SparseBinaryVector {

  private var theColIds = colIds
  private var theStartIdx = startIdx
  private var theEndIdx = endIdx

  /**
   * reset this object to point to a different vector
   * <p>
   * Note that this method should be used very sparingly -- it violates a lot of the tenants
   * of functional programming.  However, the performance benefits of avoiding recreating
   * new objects and forcing more GC are worth it sometimes
   * @param colIds
   * @param startIdx
   * @param endIdx
   */
  def reset(colIds: Array[Int], startIdx: Int, endIdx: Int) {
    theColIds = colIds
    theStartIdx = startIdx
    theEndIdx = endIdx
  }

  def dot(x: Array[Float]) : Float = {
    var f : Float = 0
    var idx = theStartIdx
    while (idx < theEndIdx) {
      f += x(theColIds(idx))
      idx += 1
    }
    f
  }

  def preMult(theta: Array[Array[Float]], into: Array[Float]) : Unit = {
    var idx = theStartIdx
    while (idx < theEndIdx) {
      val colId = theColIds(idx)
      val theta_c = theta(colId)
      var j = 0
      while (j < into.length) {
        into(j) += theta_c(j)
        j += 1
      }
      idx += 1
    }
  }

  def nnz = theEndIdx - theStartIdx
  def get(col : Int) : Int = {
    if (util.Arrays.binarySearch(theColIds, theStartIdx, theEndIdx, col) < 0)
      return 0
    else
      return 1
  }
}