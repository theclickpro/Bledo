package example.controller;

import com.theclickpro.bledo.*;

abstract class ExampleBaseController extends Controller {
	
	public void init() throws Exception
	{
		super.init();
		view.assign("TITLE", "The ClickPro.com LLC");
	}
	
}
