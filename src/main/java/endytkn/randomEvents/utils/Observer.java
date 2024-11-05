package endytkn.randomEvents.utils;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

public class Observer<T> {
    private final Set<Consumer<T>> observers = new CopyOnWriteArraySet<>();

    public void add(Consumer<T> observer) {
        observers.add(observer);
    }

    public void remove(Consumer<T> observer) {
        observers.remove(observer);
    }

    public void notify(T value) {
        for (Consumer<T> observer : observers) {
            observer.accept(value);
        }
    }
}
