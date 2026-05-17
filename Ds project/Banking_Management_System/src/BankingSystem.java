import java.util.Scanner;

// ------------------ Customer Class ------------------
class Customer {
    int accountNumber;
    String name;
    double balance;

    Customer(int accountNumber, String name, double balance) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.balance = balance;
    }

    public String toString() {
        return accountNumber + " - " + name + " - Balance: " + balance;
    }
}

// ------------------ Linked List for Transactions ------------------
class TransactionNode {
    String transaction;
    TransactionNode next;

    TransactionNode(String transaction) {
        this.transaction = transaction;
        this.next = null;
    }
}

class TransactionHistory {
    TransactionNode head;

    void addTransaction(String t) {
        TransactionNode newNode = new TransactionNode(t);
        if (head == null) head = newNode;
        else {
            TransactionNode temp = head;
            while (temp.next != null) temp = temp.next;
            temp.next = newNode;
        }
    }

    void printHistory() {
        TransactionNode temp = head;
        while (temp != null) {
            System.out.println("  -> " + temp.transaction);
            temp = temp.next;
        }
    }
}

// ------------------ Stack for Undo Operations ------------------
class TransactionStack {
    String[] stack;
    int top;

    TransactionStack(int size) {
        stack = new String[size];
        top = -1;
    }

    void push(String t) {
        if (top < stack.length - 1) stack[++top] = t;
    }

    String pop() {
        if (top >= 0) return stack[top--];
        return null;
    }
}

// ------------------ Queue for Customer Requests ------------------
class RequestQueue {
    String[] queue;
    int front, rear, size, capacity;

    RequestQueue(int capacity) {
        this.capacity = capacity;
        queue = new String[capacity];
        front = 0; rear = -1; size = 0;
    }

    void enqueue(String request) {
        if (size == capacity) return;
        rear = (rear + 1) % capacity;
        queue[rear] = request;
        size++;
    }

    String dequeue() {
        if (size == 0) return null;
        String item = queue[front];
        front = (front + 1) % capacity;
        size--;
        return item;
    }
}

// ------------------ BST for Account Lookup ------------------
class BSTNode {
    Customer customer;
    BSTNode left, right;

    BSTNode(Customer customer) {
        this.customer = customer;
        left = right = null;
    }
}

class AccountBST {
    BSTNode root;

    void insert(Customer c) {
        root = insertRec(root, c);
    }

    BSTNode insertRec(BSTNode root, Customer c) {
        if (root == null) return new BSTNode(c);
        if (c.accountNumber < root.customer.accountNumber)
            root.left = insertRec(root.left, c);
        else if (c.accountNumber > root.customer.accountNumber)
            root.right = insertRec(root.right, c);
        return root;
    }

    Customer search(int accountNumber) {
        return searchRec(root, accountNumber);
    }

    Customer searchRec(BSTNode root, int accountNumber) {
        if (root == null) return null;
        if (root.customer.accountNumber == accountNumber) return root.customer;
        if (accountNumber < root.customer.accountNumber)
            return searchRec(root.left, accountNumber);
        return searchRec(root.right, accountNumber);
    }

    // NEW: Delete method
    void delete(int accountNumber) {
        root = deleteRec(root, accountNumber);
    }

    BSTNode deleteRec(BSTNode root, int accountNumber) {
        if (root == null) return null;
        if (accountNumber < root.customer.accountNumber)
            root.left = deleteRec(root.left, accountNumber);
        else if (accountNumber > root.customer.accountNumber)
            root.right = deleteRec(root.right, accountNumber);
        else {
            // Node found
            if (root.left == null) return root.right;
            else if (root.right == null) return root.left;

            // Replace with inorder successor
            root.customer = minValue(root.right);
            root.right = deleteRec(root.right, root.customer.accountNumber);
        }
        return root;
    }

    Customer minValue(BSTNode root) {
        Customer min = root.customer;
        while (root.left != null) {
            min = root.left.customer;
            root = root.left;
        }
        return min;
    }
}

// ------------------ Bubble Sort ------------------
class Sorter {
    static void bubbleSort(Customer[] arr) {
        int n = arr.length;
        for (int i = 0; i < n-1; i++) {
            for (int j = 0; j < n-i-1; j++) {
                if (arr[j] != null && arr[j+1] != null &&
                        arr[j].balance > arr[j+1].balance) {
                    Customer temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
            }
        }
    }
}

// ------------------ Main Banking System ------------------
public class BankingSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Step 1: Add 3 customers via Scanner
        Customer[] customers = new Customer[3];
        for (int i = 0; i < 3; i++) {
            System.out.println("Enter details for Customer " + (i+1));
            System.out.print("Account Number: ");
            int acc = sc.nextInt();
            sc.nextLine(); // consume newline
            System.out.print("Name: ");
            String name = sc.nextLine();
            System.out.print("Balance: ");
            double bal = sc.nextDouble();
            customers[i] = new Customer(acc, name, bal);
        }

        // Supporting structures
        TransactionHistory history = new TransactionHistory();
        TransactionStack stack = new TransactionStack(20);
        RequestQueue queue = new RequestQueue(10);
        AccountBST bst = new AccountBST();
        for (Customer c : customers) bst.insert(c);

        // Step 2: Menu-driven system
        int choice;
        do {
            System.out.println("\n--- Banking Menu ---");
            System.out.println("1. View All Customers");
            System.out.println("2. Add Transaction");
            System.out.println("3. View Transaction History");
            System.out.println("4. Undo Last Transaction");
            System.out.println("5. Add Customer Request");
            System.out.println("6. Process Next Request");
            System.out.println("7. Search Customer by Account (BST)");
            System.out.println("8. Sort Customers by Balance (Bubble Sort)");
            System.out.println("9. Remove Customer Record");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    for (Customer c : customers) if (c != null) System.out.println(c);
                    break;
                case 2:
                    System.out.print("Enter transaction detail: ");
                    String t = sc.nextLine();
                    history.addTransaction(t);
                    stack.push("Undo: " + t);
                    break;
                case 3:
                    history.printHistory();
                    break;
                case 4:
                    System.out.println("Undo Operation: " + stack.pop());
                    break;
                case 5:
                    System.out.print("Enter request: ");
                    String req = sc.nextLine();
                    queue.enqueue(req);
                    break;
                case 6:
                    System.out.println("Processing Request: " + queue.dequeue());
                    break;
                case 7:
                    System.out.print("Enter account number to search: ");
                    int acc = sc.nextInt();
                    Customer found = bst.search(acc);
                    System.out.println(found != null ? found : "Not Found");
                    break;
                case 8:
                    Sorter.bubbleSort(customers);
                    System.out.println("Sorted Customers by Balance:");
                    for (Customer c : customers) if (c != null) System.out.println(c);
                    break;
                case 9:
                    System.out.print("Enter account number to remove: ");
                    int removeAcc = sc.nextInt();
                    for (int i = 0; i < customers.length; i++) {
                        if (customers[i] != null && customers[i].accountNumber == removeAcc) {
                            bst.delete(removeAcc);   // remove from BST too
                            customers[i] = null;     // remove from array
                            System.out.println("Customer removed.");
                            break;
                        }
                    }
                    break;
                case 0:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } while (choice != 0);

        sc.close();
    }
}
