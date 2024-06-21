package net.sasakonnect.wallet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.ResponseDto.lark.EventCallbackDto;
import net.sasakonnect.wallet.annotations.CustomController;
import net.sasakonnect.wallet.services.LarkService;

@RequestMapping("/lark")
@Tag(name = "Lark", description = "Lark services routes")
@CustomController()
@Slf4j
public class LarkController {
   @Autowired
   LarkService larkService;
	@PostMapping("/callback")
	public ResponseEntity callBack(@RequestBody() Object larkResponse) {
		log.info(larkResponse+"");
		try {
			this.larkService.replyMessageTag((EventCallbackDto)larkResponse);
		}catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.ok(larkResponse);

		}
		return ResponseEntity.ok(larkResponse);

	}
}
