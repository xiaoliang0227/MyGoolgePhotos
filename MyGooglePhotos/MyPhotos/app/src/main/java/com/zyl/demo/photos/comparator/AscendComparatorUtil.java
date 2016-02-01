package com.zyl.demo.photos.comparator;

import com.zyl.demo.photos.model.ImageItemModel;

import java.util.Comparator;

/**
 * Created by JasonZhao on 16/1/19.
 */
public class AscendComparatorUtil implements Comparator<Object> {
  /**
   * Compares the two specified objects to determine their relative ordering. The ordering
   * implied by the return value of this method for all possible pairs of
   * {@code (lhs, rhs)} should form an <i>equivalence relation</i>.
   * This means that
   * <ul>
   * <li>{@code compare(a,a)} returns zero for all {@code a}</li>
   * <li>the sign of {@code compare(a,b)} must be the opposite of the sign of {@code
   * compare(b,a)} for all pairs of (a,b)</li>
   * <li>From {@code compare(a,b) > 0} and {@code compare(b,c) > 0} it must
   * follow {@code compare(a,c) > 0} for all possible combinations of {@code
   * (a,b,c)}</li>
   * </ul>
   *
   * @param lhs an {@code Object}.
   * @param rhs a second {@code Object} to compare with {@code lhs}.
   * @return an integer < 0 if {@code lhs} is less than {@code rhs}, 0 if they are
   * equal, and > 0 if {@code lhs} is greater than {@code rhs}.
   * @throws ClassCastException if objects are not of the correct type.
   */
  @Override
  public int compare(Object lhs, Object rhs) {
    if (lhs instanceof ImageItemModel) {
      ImageItemModel model1 = (ImageItemModel) lhs;
      ImageItemModel model2 = (ImageItemModel) rhs;
      if (model1.getCreateTime() < model2.getCreateTime()) {
        return -1;
      } else if (model1.getCreateTime() > model2.getCreateTime()) {
        return 1;
      }
    }
    return 0;
  }

  /**
   * Compares this {@code Comparator} with the specified {@code Object} and indicates whether they
   * are equal. In order to be equal, {@code object} must represent the same object
   * as this instance using a class-specific comparison.
   * <p/>
   * A {@code Comparator} never needs to override this method, but may choose so for
   * performance reasons.
   *
   * @param object the {@code Object} to compare with this comparator.
   * @return boolean {@code true} if specified {@code Object} is the same as this
   * {@code Object}, and {@code false} otherwise.
   * @see Object#hashCode
   * @see Object#equals
   */
  @Override
  public boolean equals(Object object) {
    return false;
  }
}
