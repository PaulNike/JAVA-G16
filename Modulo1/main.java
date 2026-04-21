import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingresa tu nombre: ");
        String nombre = scanner.nextLine();

        System.out.print("Ingresa tu edad: ");
        int edad = scanner.nextInt();

        System.out.print("Ingresa tu estatura: ");
        double estatura = scanner.nextDouble();

        System.out.print("¿Actualmente trabajas? (true/false): ");
        boolean trabaja = scanner.nextBoolean();

        System.out.println("\nHola " + nombre);
        System.out.println("Tienes " + edad + " años");
        System.out.println("Mides " + estatura + " metros");
        System.out.println("Actualmente trabajas: " + trabaja);

        scanner.close();
    }
}