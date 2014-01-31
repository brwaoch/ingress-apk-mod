package broot.ingress.mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import broot.ingress.mod.BuildConfig.UiVariant;
import broot.ingress.mod.util.Config;
import broot.ingress.mod.util.Config.Pref;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.tablelayout.Value;
import com.nianticproject.ingress.common.scanner.ScannerStateManager;
import com.nianticproject.ingress.common.scanner.visuals.PortalParticleRender;
import com.nianticproject.ingress.common.ui.BaseSubActivity;
import com.nianticproject.ingress.common.ui.UiLayer;
import com.nianticproject.ingress.common.ui.widget.MenuTabId;
import com.nianticproject.ingress.common.ui.widget.MenuTopWidget;

public class AboutModActivity extends BaseSubActivity {
	private static Map<Pref, TextButton> buttons = new HashMap<Pref, TextButton>();

	public static class ListItem extends Table {
		public Label                   nameLabel;
		public Label                   descLabel;

		private final Skin             skin;
		private final Label.LabelStyle smallWhite;
		private Table                  buttonsTable;

		public ListItem(final Skin skin, final String name, final String desc) {
			this.skin = skin;
			smallWhite = skin.get("small-white", Label.LabelStyle.class);

			setBackground(skin.getDrawable("nav-button-clear"));

			final Table t1 = new Table();
			t1.setBackground(skin.getDrawable("nav-button"));
			t1.add(nameLabel = new Label(name, smallWhite)).pad(0, 8, 0, 8);
			add(t1).left().top().pad(8, -8, 8, 8);

			add(descLabel = new Label(desc, smallWhite)).expandX().left();
			descLabel.setWrap(true);

			row();
		}

		public ListItem(final Skin skin, final String name, final String desc, final String value,
		        final EventListener listener) {
			this(skin, name, desc);
			addButton(null, value, listener);
		}
		
		public ListItem(final Skin skin, final String name, final Pref pref, final EventListener listener) {
			this(skin, name, "");
			addButton(pref, listener);
        }

		public void addButton(final Pref pref, final EventListener listener) {
	        buttons.put(pref, addButton(pref.getDescription(), Config.getButtonText(pref), listener));
        }

		private TextButton addButton(final String name, final String value, final EventListener listener) {
			if (buttonsTable == null) {
				add(buttonsTable = new Table()).colspan(2).expandX().fillX().padBottom(8);
			}

			buttonsTable.row();
			if (name == null) {
				buttonsTable.add().expandX();
			} else {
				buttonsTable.add(new Label(name, smallWhite)).expandX().right().padRight(12);
			}

			final TextButton button = new TextButton(value, skin);
			if (listener != null) {
				button.addListener(listener);
			}
			buttonsTable.add(button).width(Value.percentWidth(0.5F)).right().padRight(8);

			return button;
		}
	}

	private MenuTopWidget topWidget;

	private Table         menuItemsTable;
	private ListItem      gameplayTweaksItem;
	private ListItem      tabsItem;
	private ListItem      animsItem;
	private ListItem      uiTweaksItem;
	private ListItem      uiVariantItem;

	private ListItem      restartItem;

	public AboutModActivity() {
		super(AboutModActivity.class.getName());

		getRenderer().addUiLayer(new UiLayer() {
			private void addItem(final ListItem item) {
				menuItemsTable.add(item).expandX().fillX().padBottom(-2);
				menuItemsTable.row();
			}

			@Override
			public void createUi(final Skin skin, final Stage stage) {
				menuItemsTable = new Table();
				menuItemsTable.top().pad(10);

				gameplayTweaksItem = new ListItem(skin, "Gameplay tweaks", null);
				gameplayTweaksItem.addButton(Pref.SwapTouchMenuButtons, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.SwapTouchMenuButtons);
						updateValues();
					}
				});
				addItem(gameplayTweaksItem);

				tabsItem = new ListItem(skin, "Menu tabs", null);
				tabsItem.addButton(Pref.ItemsTab, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.setNextValue(Pref.ItemsTab);
						updateValues();
					}
				});
				tabsItem.addButton(Pref.ShowOrigItemsTab, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.ShowOrigItemsTab);
						updateValues();
					}
				});
				tabsItem.addButton(Pref.ShowAgentTab, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.ShowAgentTab);
						updateValues();
					}
				});
				tabsItem.addButton(Pref.ShowMissionTab, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.ShowMissionTab);
						updateValues();
					}
				});
				tabsItem.addButton(Pref.ShowIntelTab, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.ShowIntelTab);
						updateValues();
					}
				});
				tabsItem.addButton(Pref.ShowRecruitTab, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.ShowRecruitTab);
						updateValues();
					}
				});
				tabsItem.addButton(Pref.ShowPasscodeTab, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.ShowPasscodeTab);
						updateValues();
					}
				});
				tabsItem.addButton(Pref.ShowDeviceTab, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.ShowDeviceTab);
						updateValues();
					}
				});
				addItem(tabsItem);

				animsItem = new ListItem(skin, "Animations", null);
				animsItem.addButton(Pref.ScannerZoomInAnimEnabled, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.ScannerZoomInAnimEnabled);
						updateValues();
					}
				});
				animsItem.addButton(Pref.HackAnimEnabled, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.HackAnimEnabled);
						updateValues();
					}
				});
				animsItem.addButton(Pref.RotateInventoryItemsEnabled, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.RotateInventoryItemsEnabled);
						updateValues();
					}
				});
				animsItem.addButton(Pref.RecycleAnimationsEnabled, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.RecycleAnimationsEnabled);
						updateValues();
					}
				});
				animsItem.addButton(Pref.XmFlowEnabled, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.XmFlowEnabled);
						updateValues();
					}
				});
				animsItem.addButton(Pref.ShieldAnimEnabled, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.ShieldAnimEnabled);
						updateValues();
					}
				});
				addItem(animsItem);

				uiTweaksItem = new ListItem(skin, "UI tweaks", null);
				uiTweaksItem.addButton(Pref.Fullscreen, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.Fullscreen);
						updateValues();
						Mod.updateFullscreenMode();
						setRestartRecommended();
					}
				});
				uiTweaksItem.addButton(Pref.ShowPortalVectors, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.ShowPortalVectors);
						updateValues();
					}
				});
				uiTweaksItem.addButton(Pref.PortalParticlesEnabled, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.PortalParticlesEnabled);
						updateValues();
						PortalParticleRender.enabled = Config.getBoolean(Pref.PortalParticlesEnabled);
					}
				});
				uiTweaksItem.addButton(Pref.ScannerObjectsEnabled, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.ScannerObjectsEnabled);
						updateValues();
					}
				});
				uiTweaksItem.addButton(Pref.SimplifyInventoryItems, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.SimplifyInventoryItems);
						updateValues();
					}
				});
				uiTweaksItem.addButton(Pref.ChatTimeFormat, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.setNextValue(Pref.ChatTimeFormat);
						updateValues();
						setRestartRecommended();
					}
				});
				uiTweaksItem.addButton(Pref.Vibration, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.Vibration);
						updateValues();
					}
				});
				uiTweaksItem.addButton(Pref.KeepScreenOn, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.KeepScreenOn);
						updateValues();
						Mod.updateKeepScreenOn();
					}
				});
//				uiTweaksItem.addButton(Pref.GpsLockTime, new ClickListener() {
//					@Override
//					public void clicked(final InputEvent event, final float x, final float y) {
//						Config.setNextValue(Pref.GpsLockTime);
//						updateValues();
//						Mod.updateKeepScreenOn();
//					}
//				});
				uiTweaksItem.addButton(Pref.ChangePortalInfoDialog, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.ChangePortalInfoDialog);
						updateValues();
					}
				});
				uiTweaksItem.addButton(Pref.AllowModRecycle, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.setNextValue(Pref.AllowModRecycle);
						updateValues();
					}
				});
				uiTweaksItem.addButton(Pref.PortalInfoDialogShowsHacksTimer, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.PortalInfoDialogShowsHacksTimer);
						updateValues();
					}
				});
				uiTweaksItem.addButton(Pref.EnablePowerCubesRecycle, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.EnablePowerCubesRecycle);
						updateValues();
					}
				});
				uiTweaksItem.addButton(Pref.IsPrivacyOn, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.IsPrivacyOn);
						updateValues();
						setRestartRecommended();
					}
				});
				uiTweaksItem.addButton(Pref.NeedInviteNagBlock, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.invertBooleanPreference(Pref.NeedInviteNagBlock);
						updateValues();
					}
				});
				addItem(uiTweaksItem);

				addItem(uiVariantItem = new ListItem(skin, "UI variant", Pref.UiVariant, new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Config.setNextValue(Pref.UiVariant);
						Mod.updateCurrUiVariant();
						updateValues();
						setRestartRecommended();
					}
				}));

				addItem(restartItem = new ListItem(skin, "Restart", "", "Restart app", new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Mod.restartApp();
					}
				}));

				addItem(new ListItem(skin, "Mod version", Mod.getFullVersion()));

				final Table root = new Table();
				root.setFillParent(true);
				topWidget = new MenuTopWidget(skin, (int) stage.getWidth(), Mod.menuController, MenuTabId.MOD_ABOUT);
				topWidget.createTabs();
				root.add(topWidget);
				root.row();
				root.add(new ScrollPane(menuItemsTable)).expand().fill().pad(2);

				stage.addActor(root);
			}

			@Override
			public void dispose() {
			}

			@Override
			public boolean dontDispose(final float f1) {
				return true;
			}
		});
	}

	@Override
	protected String getName() {
		return AboutModActivity.class.getName();
	}

	@Override
	protected void onResume() {
		updateValues();
	}

	private void setRestartRecommended() {
		restartItem.descLabel.setText("Restart is recommended");
	}

	private void updateValues(){
		for (Pref pref : buttons.keySet()){
			buttons.get(pref).setText(Config.getButtonText(pref));
		}
		topWidget.createTabs();
		UiVariant v = Config.getEnumValue(Pref.UiVariant);
		uiVariantItem.descLabel.setText(v.getDescription());
		if(!Config.getBoolean(Pref.ShowPortalVectors)) {
			ScannerStateManager.togglePortalVectors(Mod.scannerStateManager, false);
		}
	}
}
