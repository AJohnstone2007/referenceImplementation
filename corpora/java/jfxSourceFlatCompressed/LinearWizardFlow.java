package hello.dialog.wizard;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import hello.dialog.wizard.Wizard.WizardPane;
public class LinearWizardFlow implements Wizard.Flow {
private final List<WizardPane> pages;
public LinearWizardFlow( Collection<WizardPane> pages ) {
this.pages = new ArrayList<>(pages);
}
public LinearWizardFlow( WizardPane... pages ) {
this( Arrays.asList(pages));
}
@Override
public Optional<WizardPane> advance(WizardPane currentPage) {
int pageIndex = pages.indexOf(currentPage);
return Optional.ofNullable( pages.get(++pageIndex) );
}
@Override
public boolean canAdvance(WizardPane currentPage) {
int pageIndex = pages.indexOf(currentPage);
return pages.size()-1 > pageIndex;
}
}