/**
 * 
 */
package com.kohls.ibm.ocf.pca.printUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.IYRCPanelHolder;

/**
 * @author schandran
 *
 */
public class KohlesPrintReportBrowserPage extends Composite implements
		IYRCComposite {
	public static final String FORM_ID="com.kohls.ibm.ocf.pca.printUtils.KohlesPrintReportBrowserPage";
	private Composite pnlRoot = null;
	private Browser browserReports = null;
	private String url;
	/**
	 * @param parent
	 * @param style
	 */
	public KohlesPrintReportBrowserPage(Composite parent, int style, String url) {
		super(parent, style);
		this.url=url;
		initialize();
		
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setLayout(new FillLayout());
        createPnlRoot();
        this.setEnabled(true);
        this.setVisible(true);
        this.setSize(new Point(320, 180));
        setBindingForComponents();
			
	}

	private void setBindingForComponents() {
		browserReports.setUrl(url);
		
	}

	/* (non-Javadoc)
	 * @see com.yantra.yfc.rcp.IYRCComposite#getFormId()
	 */
	public String getFormId() {
		// TODO Auto-generated method stub
		return FORM_ID;
	}

	/* (non-Javadoc)
	 * @see com.yantra.yfc.rcp.IYRCComposite#getHelpId()
	 */
	public String getHelpId() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yantra.yfc.rcp.IYRCComposite#getPanelHolder()
	 */
	public IYRCPanelHolder getPanelHolder() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yantra.yfc.rcp.IYRCComposite#getRootPanel()
	 */
	public Composite getRootPanel() {
		// TODO Auto-generated method stub
		return this;
	}

	/**
	 * This method initializes pnlRoot	
	 *
	 */
	private void createPnlRoot() {
		pnlRoot = new Composite(this, SWT.NONE);
		pnlRoot.setLayout(new GridLayout());
		createBrowserReports();
	}

	/**
	 * This method initializes browserReports	
	 *
	 */
	private void createBrowserReports() {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		browserReports = new Browser(pnlRoot, SWT.NONE);
		browserReports.setLayoutData(gridData);
	}

}  //  @jve:decl-index=0:visual-constraint="10,-3"
