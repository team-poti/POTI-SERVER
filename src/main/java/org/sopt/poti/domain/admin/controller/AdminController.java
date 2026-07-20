package org.sopt.poti.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.admin.service.AdminService;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

  private final AdminService adminService;

  @GetMapping("/login")
  public String loginPage() {
    return "admin/login";
  }

  @GetMapping("/dashboard")
  public String dashboard(Model model) {
    model.addAttribute("userCount", adminService.countUsers());
    model.addAttribute("postCount", adminService.countPosts());
    model.addAttribute("orderCount", adminService.countOrders());
    return "admin/dashboard";
  }

  @GetMapping("/users")
  public String users(
      @RequestParam(defaultValue = "0") int page,
      Model model
  ) {
    PageRequest pageable = PageRequest.of(page, 20, Sort.by("id").descending());
    model.addAttribute("users", adminService.getUsers(pageable));
    return "admin/users";
  }

  @PostMapping("/users/{userId}/suspend")
  public String suspend(@PathVariable Long userId) {
    adminService.suspendUser(userId);
    return "redirect:/admin/users";
  }

  @PostMapping("/users/{userId}/unsuspend")
  public String unsuspend(@PathVariable Long userId) {
    adminService.unsuspendUser(userId);
    return "redirect:/admin/users";
  }

  @PostMapping("/users/{userId}/withdraw")
  public String forceWithdraw(@PathVariable Long userId) {
    adminService.forceWithdrawUser(userId);
    return "redirect:/admin/users";
  }

  @GetMapping("/posts")
  public String posts(
      @RequestParam(required = false) GroupBuyPostStatus status,
      @RequestParam(defaultValue = "0") int page,
      Model model
  ) {
    PageRequest pageable = PageRequest.of(page, 20, Sort.by("id").descending());
    model.addAttribute("posts", adminService.getPosts(status, pageable));
    model.addAttribute("selectedStatus", status);
    model.addAttribute("statuses", GroupBuyPostStatus.values());
    return "admin/posts";
  }

  @PostMapping("/posts/{postId}/delete")
  public String deletePost(@PathVariable Long postId) {
    adminService.deletePost(postId);
    return "redirect:/admin/posts";
  }
}
