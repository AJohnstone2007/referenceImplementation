package com.sun.glass.ui.ios;
import com.sun.glass.ui.*;
import com.sun.glass.ui.delegate.ClipboardDelegate;
import com.sun.glass.ui.delegate.MenuBarDelegate;
import com.sun.glass.ui.delegate.MenuDelegate;
import com.sun.glass.ui.delegate.MenuItemDelegate;
@SuppressWarnings({"UnusedDeclaration"})
public final class IosPlatformFactory extends PlatformFactory {
@Override
public Application createApplication(){
return new IosApplication();
}
@Override
public MenuBarDelegate createMenuBarDelegate(final MenuBar menubar) {
return new IosMenuBarDelegate();
}
@Override
public MenuDelegate createMenuDelegate(final Menu menu) {
return new IosMenuDelegate();
}
@Override
public MenuItemDelegate createMenuItemDelegate(final MenuItem item) {
return new IosMenuDelegate();
}
@Override
public ClipboardDelegate createClipboardDelegate() {
return new IosClipboardDelegate();
}
}
