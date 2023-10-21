package ensemble.samples.language.concurrency.service;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
public class GetDailySalesService extends Service<ObservableList<DailySales>> {
@Override
protected Task createTask() {
return new GetDailySalesTask();
}
}