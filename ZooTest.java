import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZooTest {

    private Zoo zoo;
    private LionCage lionCage;                      // вольєр для левів, 2 місця
    private UngulateCage<Ungulate> ungulateCage;    // вольєр для копитних, 3 місця
    private BirdCage<Bird> birdCage;                // вольєр для птахів, 2 місця

    @BeforeEach
    void setUp() {
        zoo = new Zoo();
        lionCage = new LionCage(2);
        ungulateCage = new UngulateCage<>(3);
        birdCage = new BirdCage<>(2);
    }

    //тестування вольєрів (Cage<T>) та зоопарку (Zoo)
    @Test
    @DisplayName("Cage(): новий вольєр має задану місткість і не містить тварин")
    void newCageIsEmpty() {
        assertEquals(2, lionCage.getMaxCapacity());
        assertEquals(0, lionCage.getOccupiedCount());
    }
    //перевірка що вольєр не приймає нульову або від'ємну місткість
    @Test
    @DisplayName("Cage(): нульова або від'ємна місткість викликає IllegalArgumentException")
    void nonPositiveCapacityIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new LionCage(0));
        assertThrows(IllegalArgumentException.class, () -> new BirdCage<Bird>(-1));
    }

    //додавання тварини збільшує кількість зайнятих місць
    @Test
    @DisplayName("addAnimal(): розміщення тварини збільшує кількість зайнятих місць")
    void addAnimalIncreasesOccupiedCount() {
        lionCage.addAnimal(new Lion("Lion1"));
        assertEquals(1, lionCage.getOccupiedCount());
    }
    //перевірка місткості вольєру
    @Test
    @DisplayName("addAnimal(): вольєр приймає тварин до досягнення максимальної місткості")
    void cageCanBeFilledToExactCapacity() {
        lionCage.addAnimal(new Lion("Lion1"));
        lionCage.addAnimal(new Lion("Lion2"));
        assertEquals(lionCage.getMaxCapacity(), lionCage.getOccupiedCount());
    }
    //перевірка місткості вольєру: якщо він заповнений, додавання ще однієї тварини викликає CageFullException
    @Test
    @DisplayName("addAnimal(): у заповнений вольєр викликає CageFullException")
    void addToFullCageThrows() {
        lionCage.addAnimal(new Lion("Lion1"));
        lionCage.addAnimal(new Lion("Lion2"));
        assertThrows(CageFullException.class, () -> lionCage.addAnimal(new Lion("Lion3")));
        assertEquals(2, lionCage.getOccupiedCount());       // кількість не змінилась
    }
    //додавання null викликає IllegalArgumentException
    @Test
    @DisplayName("addAnimal(null): викликає IllegalArgumentException")
    void addNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> lionCage.addAnimal(null));
    }

    //перевірка що вилучення наявної тварини зменшує кількість зайнятих місць
    @Test
    @DisplayName("removeAnimal(): вилучення наявної тварини зменшує кількість зайнятих місць")
    void removeExistingAnimal() {
        Zebra zebra = new Zebra("Zebra1");
        ungulateCage.addAnimal(zebra);
        ungulateCage.removeAnimal(zebra);
        assertEquals(0, ungulateCage.getOccupiedCount());
    }
    //перевірка вилучення тварини, якої немає у вольєрі
    @Test
    @DisplayName("removeAnimal(): якщо тварини немає у вольєрі, викликає AnimalNotFoundException")
    void removeMissingAnimalThrows() {
        assertThrows(AnimalNotFoundException.class, () -> birdCage.removeAnimal(new Eagle("Eagle1")));
    }
    //перевірка повторного вилучення тієї самої тварини
    @Test
    @DisplayName("removeAnimal(): повторне вилучення тієї самої тварини викликає AnimalNotFoundException")
    void removeSameAnimalTwiceThrows() {
        Zebra zebra = new Zebra("Zebra1");
        ungulateCage.addAnimal(zebra);
        ungulateCage.removeAnimal(zebra);
        assertThrows(AnimalNotFoundException.class, () -> ungulateCage.removeAnimal(zebra));
    }
    //перевірка звільненням місця після вилучення тварини з повного вольєра
    @Test
    @DisplayName("removeAnimal(): після вилучення з повного вольєра можна додати нову тварину")
    void removeFreesPlaceForNewAnimal() {
        Lion lion1 = new Lion("Lion1");
        lionCage.addAnimal(lion1);
        lionCage.addAnimal(new Lion("Lion2")); 
        lionCage.removeAnimal(lion1);
        assertDoesNotThrow(() -> lionCage.addAnimal(new Lion("Lion3")));
        assertEquals(2, lionCage.getOccupiedCount());
    }

    // типи вольєрів (generics)
    //у вол. копитних перевіряємо прийом і зебри і жирафи
    @Test
    @DisplayName("UngulateCage: одночасно зберігає зебру і жирафа")
    void ungulateCageAcceptsZebraAndGiraffe() {
        ungulateCage.addAnimal(new Zebra("Zebra1"));
        ungulateCage.addAnimal(new Giraffe("Giraffe1"));
        assertEquals(2, ungulateCage.getOccupiedCount());
    }
    //тест діміту місця на прикладі вольєру з орлами
    @Test
    @DisplayName("BirdCage: приймає орлів до межі, наступний орел викликає CageFullException")
    void birdCageHoldsEaglesAndOverflows() {
        birdCage.addAnimal(new Eagle("Eagle1"));
        birdCage.addAnimal(new Eagle("Eagle2"));
        assertEquals(2, birdCage.getOccupiedCount());
        assertThrows(CageFullException.class, () -> birdCage.addAnimal(new Eagle("Eagle3")));
    }
    //перевірка типізації generics 
    @Test
    @DisplayName("UngulateCage<Zebra>: розміщує зебру і зберігає її у списку тварин")
    void narrowTypeCageAcceptsOnlyZebras() {
        UngulateCage<Zebra> zebraOnly = new UngulateCage<>(2);
        Zebra zebra = new Zebra("Zebra1");
        zebraOnly.addAnimal(zebra);
        assertEquals(1, zebraOnly.getOccupiedCount());
        assertTrue(zebraOnly.getAnimals().contains(zebra));
    }

    //одразу к-сть тварин у зоопарко має бути 0
    @Test
    @DisplayName("Zoo.getCountOfAnimals(): у порожньому зоопарку 0 тварин")
    void emptyZooHasNoAnimals() {
        assertEquals(0, zoo.getCountOfAnimals());
    }
    //пеервірка підрахунку суми тварин скрізь
    @Test
    @DisplayName("Zoo.getCountOfAnimals(): сумує тварин з усіх вольєрів")
    void zooCountsAnimalsInAllCages() {
        zoo.addCage(lionCage);
        zoo.addCage(ungulateCage);
        zoo.addCage(birdCage);

        lionCage.addAnimal(new Lion("Lion1"));
        lionCage.addAnimal(new Lion("Lion2"));
        ungulateCage.addAnimal(new Zebra("Zebra1"));
        birdCage.addAnimal(new Eagle("Eagle1"));

        assertEquals(4, zoo.getCountOfAnimals());
    }
    //вилучаю тварину з одного з вол. і перевіряю автом. оновлення статистики
    @Test
    @DisplayName("Zoo.getCountOfAnimals(): враховує вилучення тварин")
    void zooCountUpdatesAfterRemoval() {
        zoo.addCage(ungulateCage);
        Zebra zebra = new Zebra("Zebra1");
        ungulateCage.addAnimal(zebra);
        ungulateCage.addAnimal(new Giraffe("Giraffe1"));
        assertEquals(2, zoo.getCountOfAnimals());

        ungulateCage.removeAnimal(zebra);
        assertEquals(1, zoo.getCountOfAnimals());
    }

    //перевірка Wildcards (Cage<?>), метод який викликається має видати повний список всіх тварин
    @Test
    @DisplayName("Zoo.getAllAnimals(): повертає тварин з усіх вольєрів одним списком")
    void getAllAnimalsCollectsFromEveryCage() {
        zoo.addCage(lionCage);
        zoo.addCage(birdCage);
        Lion lion1 = new Lion("Lion1");
        Eagle eagle1 = new Eagle("Eagle1");
        lionCage.addAnimal(lion1);
        birdCage.addAnimal(eagle1);

        List<Animal> all = zoo.getAllAnimals();
        assertEquals(2, all.size());
        assertTrue(all.contains(lion1));
        assertTrue(all.contains(eagle1));
    }
    
    //додаємо вольєр null - має бути помилка
    @Test
    @DisplayName("Zoo.addCage(null): викликає IllegalArgumentException")
    void addNullCageThrows() {
        assertThrows(IllegalArgumentException.class, () -> zoo.addCage(null));
    }
}