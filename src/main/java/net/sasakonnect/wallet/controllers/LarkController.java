package net.sasakonnect.wallet.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.annotations.CustomController;

@RequestMapping("/lark")
@Tag(name = "Lark", description = "Lark services routes")
@CustomController()
@Slf4j
public class LarkController {
  
	@PostMapping("/callback")
	public void callBack(@RequestBody() Object larkResponse) {
		log.info(larkResponse+"");
	}
}
