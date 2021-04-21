package pxc190029;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Scanner;

/**
 * Implementation of arbitrary numbers operations. 
 * @author Pengchao Cai pxc190029
 * @author Jie Su jxs190058
 * @author Feng Mi fxm150930
 * @author Linqian Zhu lxz190009
 */
public class Num implements Comparable<Num> {

    static long defaultBase = 1000000000;  // Change as needed
    long base = defaultBase;  
    long[] arr;  // array to store arbitrarily large integers
    boolean isNegative;  // boolean flag to represent negative numbers
    int len;  // actual number of elements of array that are used;  number is stored in arr[0..len-1]
    private int digits = (int) Math.log10(base);

    /**
     * Default constructor.
     */
    public Num() {}

    /**
     * Convert s to array-denoted large integer.
     * @param s
     */
    public Num(String s) {
        // Check s format.
        String regEx = "-?[1-9][\\d]*";
        if (!s.matches(regEx) && !s.equals("0")) {
            throw new NumberFormatException("Invalid number!");
        }
        if (s.charAt(0) == '-') {
            this.isNegative = true;
        }

        // Ignore leading '-' sign if there is one.
        int numLen = this.isNegative ? s.length() - 1 : s.length();
        this.len = numLen / digits + ((numLen % digits == 0) ? 0 : 1);
        this.arr = new long[len];
        StringBuilder sb;
        sb = this.isNegative ? new StringBuilder(s.substring(1)) : new StringBuilder(s);

        for (int i = 1; i <= len; i++) {
            String segment;
            if (numLen - i * digits < 0) {
                segment = sb.substring(0, numLen % digits);
            }
            else {
                segment = sb.substring(numLen - i * digits, numLen - i * digits + digits);
            }
            // Check if s is valid. If not, remove leading '0's.
            if (segment.charAt(0) == '0') {
                for (int j = 0; j < segment.length(); j++) {
                    if (segment.charAt(j) != '0' || j == segment.length() - 1) {
                        segment = segment.substring(j);
                    }
                }
            }
            this.arr[i - 1] = Long.parseLong(segment);
        }
    }
    
    /**
     * Convert Long-type number to array-denoted number.
     * @param x
     */
    public Num(long x) {
        // !without leading '0's
        if (x < 0) {
            this.isNegative = true;
            x = -x;
        }
        String strLong = String.valueOf(x);
        int lenLong = strLong.length();
        this.len = lenLong / digits + ((lenLong % digits == 0) ? 0 : 1); // Max len will be 3 if base = 1000,000,000
        this.arr = new long[this.len];
        int i = 0;
        while (x > 0) {
            this.arr[i++] = x % this.base;
            x /= this.base;
        }
    }
    
    /**
     * Performs addition of two operands.
     * @param a first operand.
     * @param b second operand.
     * @return sum.
     */
    public static Num add(Num a, Num b) {
        if (!a.isNegative && !b.isNegative) {
            return unsignAdd(a, b);
        } else if (a.isNegative && b.isNegative) {
            Num res = unsignAdd(a, b);
            res.isNegative = true;
            return res;
        } else if (a.isNegative && !b.isNegative) {
            if (a.compareTo(b) < 0) return unsignSubtract(b, a);
            else if (a.compareTo(b) > 0) {
                Num res = unsignSubtract(a, b);
                res.isNegative = true;
                return res;
            }
            else return new Num(0);
        } else {
            if (a.compareTo(b) < 0) {
                Num res = unsignSubtract(b, a);
                res.isNegative = true;
                return res;
            }
            else if (a.compareTo(b) > 0) {
                return unsignSubtract(a, b);
            }
            else return new Num(0);
        }
    }
    
    /**
     * Private function to perform unsigned adddition.
     * @param a first operand.
     * @param b second operand.
     * @return sum of a and b.
     */
    private static Num unsignAdd(Num a, Num b) {
    	// a > 0, b > 0
        Num res = new Num();
        res.len = Math.max(a.len, b.len) + 1;
        res.arr = new long[res.len];
        int carry = 0;
        int k = 0;
        for (int i = 0, j = 0; i < a.len || j < b.len; i++, j++) {
            long n1 = 0, n2 = 0;
            if (i < a.len) n1 = a.arr[i];
            if (j < b.len) n2 = b.arr[j];
            long sum = n1 + n2 + carry;
            res.arr[k++] = sum % defaultBase;
            carry = (int) (sum / defaultBase);
        }
        if (carry > 0) {
            res.arr[k] = carry;
        }
        else {
            res.len -= 1; // res.len indicates the actual number of elements in res.
        }
        return res;
    }
    
    /**
     * Perform subtraction.
     * @param a first operand.
     * @param b second operand.
     * @return difference of a and b.
     */
    public static Num subtract(Num a, Num b) {
        if (!a.isNegative && !b.isNegative) {
            if (a.compareTo(b) < 0) {
                Num res = unsignSubtract(b, a);
                res.isNegative = true;
                return res;
            } else if (a.compareTo(b) > 0) {
                return unsignSubtract(a, b);
            } else return new Num(0);
        } else if (a.isNegative && b.isNegative) {
            if (a.compareTo(b) > 0) {
                Num res = unsignSubtract(a, b);
                res.isNegative = true;
                return res;
            } else if (a.compareTo(b) < 0) {
                return unsignSubtract(b, a);
            } else return new Num(0);
        } else if (a.isNegative && !b.isNegative) {
            Num res = unsignAdd(a, b);
            res.isNegative = true;
            return res;
        } else {
            return unsignAdd(a, b);
        }
    }
    
    /**
     * Private function to perform unsigned subtraction.
     * @param a first operand.
     * @param b second operand.
     * @return sum of |a| and |b|.
     */
    private static Num unsignSubtract (Num a, Num b) {
        // By default: |a| > |b|
        Num res = new Num();
        int borrow = 0;
        res.arr = Arrays.copyOf(a.arr, a.len);
        for (int i = 0; i < res.arr.length; i++) {
            res.arr[i] -= borrow;
            if (i < b.len) {
                if (res.arr[i] >= b.arr[i]) {
                    res.arr[i] -= b.arr[i];
                    borrow = 0;
                }
                else {
                    res.arr[i] += defaultBase - b.arr[i];
                    borrow = 1;
                }
            }
            else {
            	if (res.arr[i] >= 0) borrow = 0;
            	else {
            		res.arr[i] += defaultBase;
                	borrow = 1;
            	}
            }
        }
        for (int i = res.arr.length - 1; i >= 0; i--) {
        	if (res.arr[i] != 0) {
        		res.len = i + 1;
        		break;
        	}
        }
        return res;
    }

    /**
     * Perform multiplication.
     * @param a first operand.
     * @param b second operand.
     * @return product of a and b.
     */
    public static Num product(Num a, Num b) {
    	Num zero = new Num(0);
    	if (a.compareTo(zero) == 0 || b.compareTo(zero) == 0) return zero;
        Num res = new Num();
        res.len = a.len + b.len;
        res.arr = new long[res.len];
        for (int j = 0; j < b.len; j++) {
            for (int i = 0; i < a.len; i++) {
                long sum = res.arr[i + j] + a.arr[i] * b.arr[j];
                res.arr[i + j] = sum % defaultBase;
                res.arr[i + j + 1] += sum / defaultBase;
            }
        }
        if (res.arr[res.len - 1] == 0) res.len -= 1;
        if (a.isNegative && b.isNegative || !a.isNegative && !b.isNegative)
            res.isNegative = false;
        else res.isNegative = true;
        return res;
    }

    /**
     * Perform a ^ b using divide and conquer.
     * @param a first operand.
     * @param b second operand.
     * @return a ^ b.
     */
    public static Num power(Num a, long n) throws ArithmeticException {
        Num zero = new Num(0);
        Num one = new Num(1);
        Num two = new Num(2);
        if (n <= -1) {
            if (a.compareTo(zero) == 0) throw new ArithmeticException("Undefined!");
            if (a.compareTo(two) >= 0) return zero;
            if (subtract(a, one).compareTo(zero) == 0) return one;
            one.isNegative = true;
            return one;
        }
        else if (n == 0) {
            if (a.compareTo(zero) == 0) throw new ArithmeticException("Undefined!");
            return one;
        }
        else {
            if (a.compareTo(zero) == 0) return zero;
            Num res = unsignPower(a, n);
            if (!a.isNegative || n % 2 == 0) return res;
            res.isNegative = true;
            return res;
        }
    }
    
    /**
     * Private function to perform a ^ b using divide and conquer.
     * @param a first operand.
     * @param b second operand.
     * @return a ^ b.
     */
    private static Num unsignPower(Num a, long n) {
    	// a != 0, n >= 0
    	// Recursion.
    	/*
        if (n == 0) return new Num(1); // a^0 = 1
        Num halfPower = unsignPower(a, n / 2);
        halfPower.printList();
        if (n % 2 == 0) return product(halfPower, halfPower);
        else return product(a, product(halfPower, halfPower));
        */
    	// Iteration.
    	Num tmp = a;
    	Num res = new Num(1);
    	long exp = n;
    	while (exp != 0) {
    		if (exp % 2 ==1) res = product(res, tmp);
    		tmp = product(tmp, tmp);
    		exp /= 2;
    	}
    	return res;
    }
    
    /**
     * Perform division using binary search.
     * @param a first operand.
     * @param b second operand.
     * @return  a / b.
     */
    public static Num divide(Num a, Num b) {
        Num zero = new Num(0);
        Num one = new Num(1);
        // if b = 0, throw exception
        if (b.compareTo(zero)== 0) throw new ArithmeticException("Undefined!");
        // if a = 0 or |a| < |b|, return 0
        if (a.compareTo(zero) == 0 || a.compareTo(b) < 0) return zero;
        // if |b| == 1
        if (b.compareTo(one) == 0) {
            if (a.isNegative != b.isNegative) {
                a.isNegative = true;
            }
            return a;
        }
        // binary begins
        Num lo = new Num(1);
        Num hi = new Num();
        // copy a to hi
        hi.len = a.len;
        hi.arr = new long[a.arr.length];
        int i = 0;
        for (long e : a.arr) hi.arr[i++] = e;
        Num mid = new Num();
        while (lo.compareTo(hi) < 0 ) {
            Num sum = add(hi, lo);
            mid = sum.by2();
            if (mid.compareTo(lo) == 0) break;
            Num prod = product(mid, b);
            if (prod.compareTo(a) == 0) break;
            else if (prod.compareTo(a) < 0) lo = mid;
            else hi = mid;
        }
        if (a.isNegative != b.isNegative) {
            mid.isNegative = true;
        }
        return mid;
    }
    
    /**
     * Perform a % b.
     * @param a first operand.
     * @param b second operand.
     * @return a % b.
     */
    public static Num mod (Num a, Num b) throws ArithmeticException {
    	Num zero = new Num(0);
    	if (b.compareTo(zero) == 0) throw new ArithmeticException("undefined!");
    	Num quotien = divide(a, b); // signed quotien
    	Num prod = product(quotien, b);
    	Num reminder = subtract(a, prod);
        return reminder;
    }
    
    /**
     * Perform sqrt() using binary search.
     * @param a first operand.
     * @return sqrt(a).
     */
    public static Num squareRoot(Num a) throws ArithmeticException {
    	if (a.isNegative) throw new ArithmeticException("Undefined!");
    	if (a.compareTo(new Num(0)) == 0) return new Num(0);
    	Num lo = new Num(1);
    	Num hi = new Num();
    	// copy a to hi
        hi.len = a.len;
        hi.arr = new long[a.arr.length];
        int i = 0;
        for (long e : a.arr) hi.arr[i++] = e;
        Num mid = new Num();
        while (lo.compareTo(hi) < 0) {
            Num sum = add(hi, lo);
            mid = sum.by2();
            if (mid.compareTo(lo) == 0) break;
            Num prod = product(mid, mid);
            if (prod.compareTo(a) == 0) break;
            else if (prod.compareTo(a) < 0) lo = mid;
            else hi = mid;
        }
        return mid;
    }

    /**
     * Compares |a|, |b|.
     * Return negative, zero, positive if this is less than, equal to, larger than the other.
     * @param other
     * @return an integer value.
     */
    public int compareTo(Num other) {
        if (this.len == other.len) {
            for (int i = this.len - 1; i >= 0; i--) {
                if (this.arr[i] > other.arr[i]) return 1;
                else if (this.arr[i] < other.arr[i]) return -1;
            }
            return 0;
        }
        return this.len - other.len;
    }
    
    /**
     * Output using the format "base: elements of list ...".
     * For example, if base=100, and the number stored corresponds to 10965,
     * then the output is "100: 65 9 1".
     * @return 
     */
    public void printList() {
        System.out.print(this.base + ": ");
        for (int i = 0; i < this.len; i++) {
            System.out.print(this.arr[i] + " ");
        }
        System.out.println();
    }
    
    
    // Return number to a string in defaultBase.
    public String toString() {
    	if (this.compareTo(new Num(0)) == 0) return "0";
        StringBuilder sb = new StringBuilder();
        if (isNegative) sb.append('-');
        for (int i = len - 1; i >= 0; i--) {
            if (i == len - 1) sb.append(arr[len - 1]);
            else {
                int padding = 0;
                if (arr[i] == 0) padding = digits - 1;
                else padding = digits - 1 - (int)Math.log10(arr[i]);
                while (padding > 0) {
                    sb.append('0');
                    padding--;
                }
                sb.append(arr[i]);
            }
        }
        return sb.toString();
    }

    public long base() { return base; }
    
    
    /**
     * Convert current base to a new base.
     * @param newBase
     * @return number equal to "this" number, in base=newBase.
     */
    public Num convertBase(int newBase) {
    	Num baseNum = new Num(newBase);
		Num zero = new Num(0);
		
		Num res = new Num();
		res.isNegative = this.isNegative;
		
		double logThis = Math.log10(this.base);
		double logNew = Math.log10(newBase);
		double newLen = (logThis / logNew) * this.len;
		
//		System.out.println(logThis);
//		System.out.println(logNew);
//		System.out.println(this.len);
//		System.out.println(newLen);
		
		res.len = (int) newLen + 1; // temporary length. Will be adjusted later. 
		
		res.arr = new long[res.len];
		int i = 0;
		
		// Copy this to thisCopy
		Num thisCopy = new Num(); 
		thisCopy.len = this.len;
		thisCopy.arr = new long[this.len];
		for (long e: this.arr) thisCopy.arr[i++] = e;
		i = 0;
		
		// Decide numbers in new array 
		while (thisCopy.compareTo(zero) > 0) {
			Num reminder = mod(thisCopy, baseNum);
//			System.out.println(reminder.toString());
			String s = reminder.toString();
//			if (s.charAt(0) == '-') {
//				s = s.substring(1);
//			}
			res.arr[i++] = Long.parseLong(s);
			Num quotien = divide(thisCopy, baseNum);
			thisCopy = quotien; 
		}
		for (i = res.arr.length - 1; i >= 0; i--) {
			if (res.arr[i] != 0) break;
		}
		res.len = i + 1;
		res.base = newBase;
		return res;
    }

    /**
     * Divide by 2, for using in binary search.
     * @return this / 2.
     */
    public Num by2() {
        Num zero = new Num(0);
        Num one = new Num(1);
        if (this.compareTo(zero) == 0 || this.compareTo(one) == 0) return zero;
        long carry = 0;
        Num res = new Num();
        res.len = this.len;
        res.arr = new long[res.len];
        for (int i = this.len - 1; i >= 0; i--) {
            long sum = carry * defaultBase + this.arr[i];
            res.arr[i] = sum / 2;
            if (sum % 2 == 1) carry = 1;
        }
        if (res.arr[res.len - 1] == 0) res.len -= 1;        
        return res;
    }

    /**
     * Evaluate an expression in postfix and return resulting number
     * Each string is one of: "*", "+", "-", "/", "%", "^", "0", or
     * a number: [1-9][0-9]*. There is no unary minus operator.
     * @param expr input string array.
     * @return resulting value of input string array.
     */
    public static Num evaluatePostfix(String[] expr) {
    	Deque<Num> stack = new ArrayDeque<>();
    	for (String str : expr) {
    		Num operand1 = new Num();
			Num operand2 = new Num();
    		switch(str) {
    			case "+": 
    				operand1 = stack.pop();
    				operand2 = stack.pop();
    				stack.push(add(operand2, operand1));
    				break;
    			case "-":
	    			operand1 = stack.pop();
					operand2 = stack.pop();
					stack.push(subtract(operand2, operand1));
					break;
    			case "*":
	    			operand1 = stack.pop();
					operand2 = stack.pop();
					stack.push(product(operand2, operand1));
					break;
    			case "/":
	    			operand1 = stack.pop();
					operand2 = stack.pop();
					stack.push(divide(operand2, operand1));
					break;
    			case "%":
	    			operand1 = stack.pop();
					operand2 = stack.pop();
					stack.push(mod(operand2, operand1));
					break;
    			case "^":
	    			operand1 = stack.pop();
					operand2 = stack.pop();
					long longOperand = Long.parseLong(operand1.toString());
					stack.push(power(operand2, longOperand));
					break;
				default: stack.push(new Num(str));
    		}
    	}
        return stack.peek();
    }

    // Parse/evaluate an expression in infix and return resulting number
    // Input expression is a string, e.g., "(3 + 4) * 5"
    // Tokenize the string and then input them to parser
    // Implementing this method correctly earns you an excellence credit
    public static Num evaluateExp(String expr) {
        return null;
    }


    public static void main(String[] args) {
	while(true) {
		System.out.println("Please enter your choice: " + "\n"
				+ "1: Binary Operation (+,-,*,/,%,x^y) " + "\n" 
				+ "2: Unary Operation (sqrt)" + "\n" 
				+ "3: Covert Base" + "\n" 
				+ "4: Evaluate Expression" + "\n"
				+ "0: To quit");
		Scanner in = new Scanner(System.in);
		int choice  = in.nextInt();
		switch (choice) {
		case 1: 
			System.out.println("Please enter two numbers a and b: " + "\n" 
					+ "(To make full use of the functionlites" + "\n"
					+ "(Make sure a is a string-represented arbitrary long number and b is a Long-type number)");
			String tmp1 = in.next();
			long tmp2 = in.nextLong();
			Num x = new Num(tmp1);
			Num y = new Num(tmp2);
			
	        Num sum = add(x, y);
	        System.out.println("a + b = " + sum.toString());
	        System.out.println("Array representation is: ");
	        sum.printList();
	        System.out.println();
	        
	        Num diff = subtract(x, y);
	        System.out.println("a - b = " + diff.toString());
	        System.out.println("Array representation is: ");
	        diff.printList();
	        System.out.println();
	        
	        Num product = product(x, y);
	        System.out.println("a * b = " + product.toString());
	        System.out.println("Array representation is: ");
	        product.printList();
	        System.out.println();
	        
	        Num quotien = divide(x, y);
	        System.out.println("a / b = " + quotien.toString());
	        System.out.println("Array representation is: ");
	        quotien.printList();
	        System.out.println();
	        
	        Num power = power(x, Long.valueOf(tmp2));
	        System.out.println("a ^ b = " + power.toString());
	        System.out.println("Array representation is: ");
	        power.printList();
	        System.out.println();
	        
	        Num reminder = mod(x, y);
	        System.out.println("a % b = " + reminder.toString());
	        System.out.println("Array representation is: ");
	        reminder.printList();
	        System.out.println();
	        
	        break;
		case 2:
			System.out.println("Please enter a long number: ");
			String tmp3 = in.next();
			Num sqrt = squareRoot(new Num(tmp3));
			System.out.println("The square root is: " + sqrt.toString() );
			System.out.println("Array representation is: ");
			sqrt.printList();
			System.out.println();
			break;
		case 3: 
			System.out.println("Please enter a long number and a new base: ");
			String tmp4 = in.next();
			int tmp5 = in.nextInt();
			Num curNum = new Num(tmp4);
			System.out.println("The input with current base is: " );
			curNum.printList();
			Num converted = curNum.convertBase(tmp5);
			System.out.println("The same number with new base is: ");
			converted.printList();
			System.out.println();
			break;
		case 4:
			System.out.println("Please enter a valid postfix expression (Use comma to seperate):"
					+ "\n [for example: \"-5\",\"3\",\"%\",\"1\",\"2\",\"+\",\"^\"]");
		    String input = in.next();
		    String arr[] = input.split(",");
		    Num eval = evaluatePostfix(arr);
		    System.out.println("Evaluated is: " + eval.toString() );
			System.out.println("Array representation is: ");
			eval.printList();
			System.out.println();
			break;
		case 0: break;
		default: {
			System.out.println("Please enter one of these choices!");
		}
		}
		if (choice == 0) break;
		
	}
	
    }
}