package net.sasakonnect.wallet.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SiteController implements ErrorController {
	@GetMapping("/error")
	public String handleError() {
		// You can return the name of your custom 404 error page here
		return "custom404";
	}

}
