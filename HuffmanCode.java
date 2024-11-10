// James Parrott
// 05/31/2023
// CSE 123
// TA: Eric Bae
// This class represents a Huffman code implementation.
// It provides methods for constructing a Huffman tree based on given frequencies,
// saving the Huffman tree code to an output stream, translating bits from an input
// stream to characters using the Huffman tree.
// The class writes out the Huffman codes to a .code file, reads the file, and
// write out a .new file.
import java.util.*;
import java.io.*;

public class HuffmanCode {
    private HuffmanNode overallRoot;

    // A constructor that builds a Huffman tree based on the given ASCII value frequencies.
    // Frequencies will be represented as the count of the character with the ASCII value.
    // If a character with a frequency is below or at zero, the character should not be
    // included in the Huffman tree.
    // Parameters:
    // - frequencies: an array of frequencies for each character
    public HuffmanCode(int frequencies[]) {
        Queue<HuffmanNode> valuePriority = new PriorityQueue<HuffmanNode>();

        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] > 0) {
                valuePriority.add(new HuffmanNode(i, frequencies[i]));
            }
        }

        while (valuePriority.size() > 1) {
            HuffmanNode first = valuePriority.remove();
            HuffmanNode second = valuePriority.remove();
            valuePriority.add(new HuffmanNode(-1, first.freq + second.freq, first, second));
        }
        overallRoot = valuePriority.remove();
    }

    // A constructor that builds a Huffman tree based on the given input.
    // It will create Huffman tree based in the previous code from a .code file.
    // There will be an assumption that the Scanner is not null
    // and is always contains data encoded in valid format.
    // Parameters:
    // - input: a Scanner object reading from the input source
    public HuffmanCode(Scanner input) {
        while (input.hasNextLine()) {
            int asciiValue = Integer.parseInt(input.nextLine());
            String code = input.nextLine();
            overallRoot = HuffmanCodeHelper(overallRoot, asciiValue, code);
        }
    }

    // Helper method to recursively build the Huffman tree based on the given
    // ASCII value and code.
    // Parameters:
    // - root: the current root node of the Huffman tree
    // - asciiValue: the ASCII value of the character
    // - code: the Huffman code for the character
    // Returns:
    // - HuffmanNode: the updated root node of the Huffman tree
    private HuffmanNode HuffmanCodeHelper(HuffmanNode root, int asciiValue, String code) {
        if (code.length() == 0) {
            // Create a leaf node with the given ASCII value and no subtree
            return new HuffmanNode(asciiValue, 0, null, null);
        } else {
            if (root == null) {
                root = new HuffmanNode(-1, 0, null, null);
            }
            if (code.charAt(0) == '0') {
                // Traverse the left subtree if the next bit is 0
                root.left = HuffmanCodeHelper(root.left, asciiValue, code.substring(1));
            } else {
                // Traverse the right subtree if the next bit is 1
                root.right = HuffmanCodeHelper(root.right, asciiValue, code.substring(1));
            }
            return root;
        }
    }

    // Saves the Huffman tree coe to the given output stream in standard form.
    // Parameters:
    // - output: a PrintStream object representing the output stream
    public void save(PrintStream output) {
        String code = "";
        traverseTree(output, code, overallRoot);
    }

    // A helper method to traverse the Huffman tree and write the ASCII value and Huffman
    // code to the output stream.
    // Parameters:
    // - output: representing the output stream
    // - code: the current Huffman code
    // - root: the current root node of the Huffman tree
    private void traverseTree(PrintStream output, String code, HuffmanNode root) {
        if (root != null) {
            if (root.left == null && root.right == null) {
                // Reached a leaf node, write the ASCII value and Huffman code to the output stream
                output.println(root.getAsciiValue());
                output.println(code);
            } else {
                // Traverse the left subtree with '0' appended to the current Huffman code
                traverseTree(output, code + "0", root.left);
                // Traverse the right subtree with '1' appended to the current Huffman code
                traverseTree(output, code + "1", root.right);
            }
        }
    }

    // Translate the bits from the input stream to characters using the
    // Huffman tree and write them to the output stream.
    // It will stop reading the bits once the BitInputStream is empty. There will be an
    // assumption that the input stream contains a legal encoding of characters for this
    // tree’s Huffman Code.
    // Parameters:
    // - input: an representing the input stream
    // - output: an object representing the output stream
    public void translate(BitInputStream input, PrintStream output) {
        while (input.hasNextBit()) {
            translate(input, output, overallRoot);
        }
    }

    // A helper method to recursively translate bits to characters using the Huffman tree and
    // write them to the output stream.
    // Translates the bits from the input stream to characters using the
    // Huffman tree and write them to the output stream.
    // It will stop reading the bits once the BitInputStream is empty. There will be an
    // assumption that the input stream contains a legal encoding of characters for this
    // tree’s Huffman Code.
    // Parameters:
    // - input: an object representing the input stream
    // - output: an object representing the output stream
    // - root: the current root node of the Huffman tree
    private void translate(BitInputStream input, PrintStream output, HuffmanNode root) {
        if (root.left == null && root.right == null) {
            // Reached a leaf node, write the character to the output stream
            output.write((char) root.getAsciiValue());
        } else {
            int nextBit = input.nextBit();
            if (nextBit == 0) {
                // Traverse the left subtree if the next bit is 0
                translate(input, output, root.left);
            } else {
                // Traverse the right subtree if the next bit is 1
                translate(input, output, root.right);
            }
        }
    }

    // Class that represents a single node in the Huffman tree
    private static class HuffmanNode implements Comparable<HuffmanNode> {
        public int asciiValue;
        public int freq;
        public HuffmanNode left;
        public HuffmanNode right;

        // Constructs a leaf node with the given ASCII value and frequency that does not have a subtree.
        // Parameters:
        // - asciiValue: the ASCII value of the character
        // - freq: the frequency of the character
        public HuffmanNode(int asciiValue, int freq) {
            this(asciiValue, freq, null, null);
        }

        // Constructs a leaf or branch node with the given ASCII value, frequency, and the left and right subtrees.
        // Parameters:
        // - asciiValue: the ASCII value of the character
        // - freq: the frequency of the character
        // - left: the left subtree
        // - right: the right subtree
        public HuffmanNode(int asciiValue, int freq, HuffmanNode left, HuffmanNode right) {
            this.asciiValue = asciiValue;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        // Get the ASCII value of the character.
        // Returns:
        // - int: the ASCII value
        public int getAsciiValue() {
            return this.asciiValue;
        }

        // Compare two Huffman nodes based on their frequencies.
        // Parameters:
        // - other: the other HuffmanNode to compare
        // Returns:
        // - int: a negative integer if this node has a lower frequency,
        //         0 if the frequencies are equal,
        //         a positive integer if this node has a higher frequency
        public int compareTo(HuffmanNode other) {
            return this.freq - other.freq;
        }
    }
}
