package com.kohls.inventory.api;

import java.rmi.RemoteException;
import java.util.Properties;

import org.w3c.dom.Document;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsCreateInventoryActivityWrapperAPI implements YIFCustomApi {
	
	private YIFApi api;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsInventoryAdjWrapperAPI.class.getName());
	@SuppressWarnings("unused")
	private Properties properties = new Properties();
	
	public KohlsCreateInventoryActivityWrapperAPI() throws YIFClientCreationException {
		this.api = YIFClientFactory.getInstance().getLocalApi();
	}
	
	@Override
	public void setProperties(Properties props) throws Exception {
		// TODO Auto-generated method stub
		this.properties = props;
		
	}
	/**
	 * This method is to error handling of YFS10437 error code 
	 * while calling createInventoryActivityAPI 
	 * @param env
	 * @param inXML
	 * @return
	 * Author	: OASIS IBM Team
	 * Date 	: 2/14/2014
	 * PMR		: RTAM GIV issue
	 */
	public Document createInventoryActivity(YFSEnvironment env, Document inXML)  {
		
		
		Document docCreateInventoryActivity=null;
		try {
				//Call createInventoryActivity API
				docCreateInventoryActivity = this.api.createInventoryActivity(env, inXML);
				
			} catch (YFSException e) {
				// TODO Auto-generated catch block
				if(e.getErrorCode().equalsIgnoreCase("YFS10437") ){
					log.debug("Item(Item Id and UOM) does not exist" );
				}else{
					e.printStackTrace();
					throw e;
				}
				
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
			return docCreateInventoryActivity;	
		
		
	}
}
