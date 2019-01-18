package com.smartgwt.sample.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.core.KeyIdentifier;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortArrow;
import com.smartgwt.client.util.Page;
import com.smartgwt.client.util.PageKeyHandler;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.viewer.DetailViewer;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
@JsType
public class BuiltInDS implements EntryPoint {
	private ListGrid boundList;
	private DynamicForm boundForm;
	private IButton saveBtn;
	private DetailViewer boundViewer;
	private IButton newBtn;
	private ListGrid grid;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		initializeLayout();

		// --------------------------------------------------------------------------------------------------------- //
		// Remove all between these lines ->
		// --------------------------------------------------------------------------------------------------------- //
		Registry.register("BuiltInDS", this);
		Registry.register("MainListGrid", grid);

		initializeWorld();
		// --------------------------------------------------------------------------------------------------------- //
		// <- Remove all between these lines
		// --------------------------------------------------------------------------------------------------------- //
	}

	// --------------------------------------------------------------------------------------------------------- //
	// Remove all between these lines ->
	// --------------------------------------------------------------------------------------------------------- //
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// JavaScript native functions
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	@JsMethod(namespace = JsPackage.GLOBAL)
	public static native void initializeWorld();


	@JsType(namespace = "j2js")
	public static class Registry {
		private static Map<String, Object> shared = new HashMap<>();

		public static void register(String name, Object o) {
			shared.put(name, o);
		}

		public static Object lookup(String name) {
			return shared.get(name);
		}
	}


	public void addJSData(JavaScriptObject jsObj){
		// conceptually:
		// this.grid.addData(new DSRecord(jsObj));

		// in reality:
		DSRecord dsRecord = new DSRecord(jsObj);
		String name = dsRecord.getDsName();
		String title = dsRecord.getDsTitle();

		// Workaround: rewrap the DSRecord
		this.grid.addData(new DSRecord(title, name));
	}	
	
	// --------------------------------------------------------------------------------------------------------- //
	// <- Remove all between these lines
	// --------------------------------------------------------------------------------------------------------- //


	private void initializeLayout() {
		KeyIdentifier debugKey = new KeyIdentifier();
		debugKey.setCtrlKey(true);
		debugKey.setKeyName("D");

		Page.registerKey(debugKey, new PageKeyHandler() {
			public void execute(String keyName) {
				SC.showConsole();
			}
		});


		grid = new ListGrid();
		grid.setLeft(20);
		grid.setTop(75);
		grid.setWidth(130);
		grid.setLeaveScrollbarGap(false);
		grid.setShowSortArrow(SortArrow.NONE);
		grid.setCanSort(false);
		grid.setFields(new ListGridField("dsTitle", "Select a DataSource"));
		grid.addData(new DSRecord("Animals", "animals"));
		grid.addData(new DSRecord("Office Supplies", "supplyItem"));
		grid.addData(new DSRecord("Employees", "employees"));
		grid.setSelectionType(SelectionStyle.SINGLE);
		grid.addRecordClickHandler(new RecordClickHandler() {
			public void onRecordClick(RecordClickEvent event) {
				DSRecord record = (DSRecord) event.getRecord();
				bindComponents(record.getDsName());
			}
		});

		grid.draw();

		VStack vStack = new VStack();
		vStack.setLeft(175);
		vStack.setTop(75);
		vStack.setWidth("70%");
		vStack.setMembersMargin(20);

		Label label = new Label();
		label.setContents("<ul>" +
				"<li>select a datasource from the list at left to bind to these components</li>" +
				"<li>click a record in the grid to view and edit that record in the form</li>" +
				"<li>click <b>New</b> to start editing a new record in the form</li>" +
				"<li>click <b>Save</b> to save changes to a new or edited record in the form</li>" +
				"<li>click <b>Clear</b> to clear all fields in the form</li>" +
				"<li>click <b>Filter</b> to filter (substring match) the grid based on form values</li>" +
				"<li>click <b>Fetch</b> to fetch records (exact match) for the grid based on form values</li>" +
				"<li>double-click a record in the grid to edit inline (press Return, or arrow/tab to another record, to save)</li>" +
				"</ul>");
		vStack.addMember(label);

		boundList = new ListGrid();
		boundList.setHeight(200);
		boundList.setCanEdit(true);

		boundList.addRecordClickHandler(new RecordClickHandler() {
			public void onRecordClick(RecordClickEvent event) {
				Record record = event.getRecord();
				boundForm.editRecord(record);
				saveBtn.enable();
				boundViewer.viewSelectedData(boundList);
			}
		});
		vStack.addMember(boundList);

		boundForm = new DynamicForm();
		boundForm.setNumCols(6);
		boundForm.setAutoFocus(false);
		vStack.addMember(boundForm);

		HLayout hLayout = new HLayout(10);
		hLayout.setMembersMargin(10);
		hLayout.setHeight(22);

		saveBtn = new IButton("Save");
		saveBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				boundForm.saveData(new DSCallback() {
					@Override
					public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
						if (dsResponse.getStatus() == DSResponse.STATUS_SUCCESS) {
							// if the save succeeded, clear the UI
							boundForm.clearValues();
							saveBtn.disable();
						}
					}
				});
			}
		});
		hLayout.addMember(saveBtn);

		newBtn = new IButton("New");
		newBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				boundForm.editNewRecord();
				saveBtn.enable();
			}
		});
		hLayout.addMember(newBtn);

		IButton clearBtn = new IButton("Clear");
		clearBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				boundForm.clearValues();
				saveBtn.disable();
			}
		});
		hLayout.addMember(clearBtn);

		IButton filterBtn = new IButton("Filter");
		filterBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				boundList.filterData(boundForm.getValuesAsCriteria());
				saveBtn.disable();
			}
		});
		hLayout.addMember(filterBtn);

		IButton fetchBtn = new IButton("Fetch");
		fetchBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				boundList.fetchData(boundForm.getValuesAsCriteria());
				saveBtn.disable();
			}
		});
		hLayout.addMember(fetchBtn);

		vStack.addMember(hLayout);

		boundViewer = new DetailViewer();
		vStack.addMember(boundViewer);

		vStack.draw();
	}

	private void bindComponents(String dsName) {
		DataSource ds = DataSource.get(dsName);
		boundList.setDataSource(ds);
		boundViewer.setDataSource(ds);
		boundForm.setDataSource(ds);
		boundList.fetchData();
		newBtn.enable();
		saveBtn.disable();
	}

}
