package org.uma.jmetal.util.point.impl;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.point.Point;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class ArrayPointTest {
  private static final double EPSILON = 0.0000000000001 ;

  @Test
  public void shouldConstructAPointOfAGivenDimension() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    double[] pointDimensions = (double[])ReflectionTestUtils.getField(point, "point");

    double[] expectedArray = {0.0, 0.0, 0.0, 0.0, 0.0} ;
    assertArrayEquals(expectedArray, pointDimensions, EPSILON);
  }

  @Test
  public void shouldConstructAPointFromOtherPointReturnAnIdenticalPoint() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;
    point.setValue(0, 1.0);
    point.setValue(1, -2.0);
    point.setValue(2, 45.5);
    point.setValue(3, -323.234);
    point.setValue(4, 2344234.23424);

    Point newPoint = new ArrayPoint(point) ;

    double[] expectedArray = {1.0, -2.0, 45.5, -323.234, 2344234.23424} ;
    double[] newPointDimensions = (double[])ReflectionTestUtils.getField(newPoint, "point");

    assertArrayEquals(expectedArray, newPointDimensions, EPSILON);

    assertEquals(point, newPoint) ;
  }

  @Test (expected = NullParameterException.class)
  public void shouldConstructAPointFromANullPointRaiseAnException() {
    Point point = null ;

    new ArrayPoint(point) ;
  }

  @Test
  public void shouldConstructFromASolutionReturnTheCorrectPoint() {
    DoubleProblem problem = new DummyDoubleProblem(3, 3, 0) ;
    DoubleSolution solution = problem.createSolution() ;
    solution.objectives()[0] = 0.2 ;
    solution.objectives()[1] = 234.23 ;
    solution.objectives()[2] = -234.2356 ;

    Point point = new ArrayPoint(solution.objectives()) ;

    double[] expectedArray = {0.2, 234.23, -234.2356} ;
    double[] pointDimensions = (double[])ReflectionTestUtils.getField(point, "point");

    assertArrayEquals(expectedArray, pointDimensions, EPSILON);

//    Mockito.verify(solution).objectives().length ;
//    Mockito.verify(solution, Mockito.times(3)).getObjective(Mockito.anyInt());
  }

  @Test
  public void shouldConstructFromArrayReturnTheCorrectPoint() {
    double[] array = {0.2, 234.23, -234.2356} ;
    Point point = new ArrayPoint(array) ;

    double[] storedValues = (double[])ReflectionTestUtils.getField(point, "point");

    assertArrayEquals(array, storedValues, EPSILON);
  }

  @Test (expected = NullParameterException.class)
  public void shouldConstructFromNullArrayRaiseAnException() {
    double[] array = null ;

    new ArrayPoint(array) ;
  }

  @Test
  public void shouldGetNumberOfDimensionsReturnTheCorrectValue() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    assertEquals(dimension, point.getDimension());
  }

  @Test
  public void shouldGetValuesReturnTheCorrectValues() {
    int dimension = 5 ;
    double[] array = {1.0, -2.0, 45.5, -323.234, 2344234.23424} ;

    Point point = new ArrayPoint(dimension) ;
    ReflectionTestUtils.setField(point, "point", array);

    assertArrayEquals(array, point.getValues(), EPSILON);
  }

  @Test
  public void shouldGetDimensionValueReturnTheCorrectValue() {
    int dimension = 5 ;
    double[] array = {1.0, -2.0, 45.5, -323.234, Double.MAX_VALUE} ;

    Point point = new ArrayPoint(dimension) ;
    ReflectionTestUtils.setField(point, "point", array);

    assertEquals(1.0, point.getValue(0), EPSILON) ;
    assertEquals(-2.0, point.getValue(1), EPSILON) ;
    assertEquals(45.5, point.getValue(2), EPSILON) ;
    assertEquals(-323.234, point.getValue(3), EPSILON) ;
    assertEquals(Double.MAX_VALUE, point.getValue(4), EPSILON) ;
  }

  @Test (expected = InvalidConditionException.class)
  public void shouldGetDimensionValueWithInvalidIndexesRaiseAnException() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    point.getValue(-1) ;
    point.getValue(5) ;
  }

  @Test
  public void shouldSetDimensionValueAssignTheCorrectValue() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;
    point.setValue(0, 1.0);
    point.setValue(1, -2.0);
    point.setValue(2, 45.5);
    point.setValue(3, -323.234);
    point.setValue(4, Double.MAX_VALUE);

    double[] array = {1.0, -2.0, 45.5, -323.234, Double.MAX_VALUE} ;

    assertArrayEquals(array, (double[])ReflectionTestUtils.getField(point, "point"), EPSILON);
  }

  @Test (expected = InvalidConditionException.class)
  public void shouldSetDimensionValueWithInvalidIndexesRaiseAnException() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    point.setValue(-1, 2.2) ;
    point.setValue(5, 2.0) ;
  }

  @Test
  public void shouldEqualsReturnTrueIfThePointsAreIdentical() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;
    point.setValue(0, 1.0);
    point.setValue(1, -2.0);
    point.setValue(2, 45.5);
    point.setValue(3, -323.234);
    point.setValue(4, Double.MAX_VALUE);

    Point newPoint = new ArrayPoint(point) ;

    assertTrue(point.equals(newPoint));
  }

  @Test
  public void shouldEqualsReturnTrueIfTheTwoPointsAreTheSame() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    assertTrue(point.equals(point));
  }


  @Test
  public void shouldEqualsReturnFalseIfThePointsAreNotIdentical() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;
    point.setValue(0, 1.0);
    point.setValue(1, -2.0);
    point.setValue(2, 45.5);
    point.setValue(3, -323.234);
    point.setValue(4, Double.MAX_VALUE);

    Point newPoint = new ArrayPoint(point) ;
    newPoint.setValue(0, -1.0);

    assertFalse(point.equals(newPoint));
  }

 @Test
 public void shouldSetAssignTheRightValues() {
   Point point = new ArrayPoint(new double[]{2, 3, 3}) ;

   point.set(new double[]{5, 6, 7}) ;
   assertEquals(5, point.getValue(0), EPSILON);
   assertEquals(6, point.getValue(1), EPSILON);
   assertEquals(7, point.getValue(2), EPSILON);
 }

  @Test
  public void shouldEqualsReturnFalseIfThePointIsNull() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    assertFalse(point.equals(null));
  }

  @Test
  public void shouldEqualsReturnFalseIfTheClassIsNotAPoint() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

      assertFalse(point.equals(""));
  }

  @Test
  public void shouldHashCodeReturnTheCorrectValue() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    point.setValue(0, 1.0);
    point.setValue(1, -2.0);
    point.setValue(2, 45.5);
    point.setValue(3, -323.234);
    point.setValue(4, Double.MAX_VALUE);

    double[] array = {1.0, -2.0, 45.5, -323.234, Double.MAX_VALUE} ;

    assertEquals(Arrays.hashCode(array), point.hashCode());
  }
}
