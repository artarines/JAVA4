import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//виняток: у вольєрі немає вільних місць
class CageFullException extends RuntimeException {
    public CageFullException(String message) { super(message); }
}

//виняток: указаної тварини немає у вольєрі
class AnimalNotFoundException extends RuntimeException {
    public AnimalNotFoundException(String message) { super(message); }
}

//будь-яка тварина має ім'я 
abstract class Animal {
    private final String name;

    public Animal(String name) { this.name = name; }

    public String getName() { return name; }

    // Наприклад: "Lion (Lion1))"
    @Override
    public String toString() { return this.getClass().getSimpleName() + " (" + name + ")"; }
}

//mammal
abstract class Mammal extends Animal {
    public Mammal(String name) { super(name); }
}

//birds
abstract class Bird extends Animal {
    public Bird(String name) { super(name); }
}
// lion=mamal
class Lion extends Mammal {
    public Lion(String name) { super(name); }
}

//копитні-ссавці; від них зебра та жираф.
abstract class Ungulate extends Mammal {
    public Ungulate(String name) { super(name); }
}

class Zebra extends Ungulate {
    public Zebra(String name) { super(name); }
}

class Giraffe extends Ungulate {
    public Giraffe(String name) { super(name); }
}

class Eagle extends Bird {
    public Eagle(String name) { super(name); }
}


// базовий вольєр. параметр узагальнення T extends Animal означає: у вольєрі можуть бути лише тварини типу T (або його нащадки)
abstract class Cage<T extends Animal> {
    private final int maxCapacity;      // максимальна кількість місць
    private final List<T> animals;      // private: нащадки працюють через методи базового класу

    public Cage(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Місткість вольєра має бути додатною: " + maxCapacity);
        }
        this.maxCapacity = maxCapacity;
        this.animals = new ArrayList<>();
    }
    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getOccupiedCount() {
        return animals.size();
    }

    public List<T> getAnimals() {
        return Collections.unmodifiableList(animals);
    }

    //розмістити тварину,!якщо місць немає CageFullException
    public void addAnimal(T animal) {
        if (animal == null) {
            throw new IllegalArgumentException("Тварина не може бути null");
        }
        if (animals.size() >= maxCapacity) {
            throw new CageFullException("Вольєр заповнений! Неможливо додати: " + animal.getName());
        }
        animals.add(animal);
    }

    // вилучити тварину, !якщо її немає у вольєрі AnimalNotFoundException. 
    public void removeAnimal(T animal) {
        if (animal == null) {
            throw new IllegalArgumentException("Тварина не може бути null");
        }
        if (!animals.remove(animal)) {
            throw new AnimalNotFoundException("Тварину " + animal.getName() + " не знайдено у цьому вольєрі!");
        }
    }
}


class MammalCage<T extends Mammal> extends Cage<T> {
    public MammalCage(int maxCapacity) { super(maxCapacity); }
}

class BirdCage<T extends Bird> extends Cage<T> {
    public BirdCage(int maxCapacity) { super(maxCapacity); }
}

//вольєр для левів, тип зафіксовано Lion. Інших тварин компілятор не пустить
class LionCage extends MammalCage<Lion> {
    public LionCage(int maxCapacity) { super(maxCapacity); }
}

/** для копитних: лише зебри та жирафи. */
class UngulateCage<T extends Ungulate> extends MammalCage<T> {
    public UngulateCage(int maxCapacity) { super(maxCapacity); }
}

class Zoo {
    // Cage<?> — wildcard: у списку можуть бути вольєри для будь-яких тварин
    public List<Cage<?>> cages = new ArrayList<>();

    // + вольєр до зоо приймає вольєр будь-якого типу
    public void addCage(Cage<?> cage) {
        if (cage == null) {
            throw new IllegalArgumentException("Вольєр не може бути null");
        }
        cages.add(cage);
    }

    // загальна кількість тварин у всіх вольєрах
    public int getCountOfAnimals() {
        int count = 0;
        for (Cage<?> cage : cages) {
            count += cage.getOccupiedCount();
        }
        return count;
    }

    public List<Animal> getAllAnimals() {
        List<Animal> result = new ArrayList<>();
        for (Cage<? extends Animal> cage : cages) {
            result.addAll(cage.getAnimals());
        }
        return result;
    }
}
//візуал тестів з файду ZooTest 
public class java4 {
    public static void main(String[] args) {

        Zoo zoo = new Zoo();
        LionCage lions = new LionCage(1);
        UngulateCage<Ungulate> ungulates = new UngulateCage<>(2);
        BirdCage<Bird> birds = new BirdCage<>(1);
        zoo.addCage(lions);
        zoo.addCage(ungulates);
        zoo.addCage(birds);

        lions.addAnimal(new Lion("Lion1"));
        ungulates.addAnimal(new Zebra("Zebra1"));
        ungulates.addAnimal(new Giraffe("Giraffe1"));
        birds.addAnimal(new Eagle("Eagle1"));

        System.out.println("Тварини: " + zoo.getAllAnimals());
        System.out.println("Загальна кількість тварин: " + zoo.getCountOfAnimals());

        try {
            lions.addAnimal(new Lion("Lion2"));
        } catch (CageFullException e) {
            System.out.println("Перехоплено виняток: " + e.getMessage());
        }

        try {
            birds.removeAnimal(new Eagle("Eagle2"));
        } catch (AnimalNotFoundException e) {
            System.out.println("Перехоплено виняток: " + e.getMessage());
        }

        System.out.println("Модульні тести у файлі ZooTest.java і запускаються через JUnit.");
    }
}