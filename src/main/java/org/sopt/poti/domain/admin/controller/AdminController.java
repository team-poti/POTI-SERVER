package org.sopt.poti.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.admin.service.AdminService;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.sopt.poti.global.error.BusinessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    model.addAttribute("artistCount", adminService.countArtists());
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
  public String suspend(@PathVariable Long userId, RedirectAttributes ra) {
    try {
      adminService.suspendUser(userId);
    } catch (BusinessException e) {
      ra.addFlashAttribute("errorMessage", e.getErrorStatus().getMessage());
    }
    return "redirect:/admin/users";
  }

  @PostMapping("/users/{userId}/unsuspend")
  public String unsuspend(@PathVariable Long userId, RedirectAttributes ra) {
    try {
      adminService.unsuspendUser(userId);
    } catch (BusinessException e) {
      ra.addFlashAttribute("errorMessage", e.getErrorStatus().getMessage());
    }
    return "redirect:/admin/users";
  }

  @PostMapping("/users/{userId}/withdraw")
  public String forceWithdraw(@PathVariable Long userId, RedirectAttributes ra) {
    try {
      adminService.forceWithdrawUser(userId);
    } catch (BusinessException e) {
      ra.addFlashAttribute("errorMessage", e.getErrorStatus().getMessage());
    }
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
  public String deletePost(@PathVariable Long postId, RedirectAttributes ra) {
    try {
      adminService.deletePost(postId);
    } catch (BusinessException e) {
      ra.addFlashAttribute("errorMessage", e.getErrorStatus().getMessage());
    }
    return "redirect:/admin/posts";
  }

  @GetMapping("/artists")
  public String artists(Model model) {
    model.addAttribute("artists", adminService.getArtists());
    model.addAttribute("postCounts", adminService.getArtistPostCounts());
    return "admin/artists";
  }

  @PostMapping("/artists")
  public String createArtist(
      @RequestParam String name,
      @RequestParam(defaultValue = "") String logoImageUrl,
      RedirectAttributes ra
  ) {
    try {
      adminService.createArtist(name.strip(), logoImageUrl.strip());
    } catch (BusinessException e) {
      ra.addFlashAttribute("errorMessage", e.getErrorStatus().getMessage());
    }
    return "redirect:/admin/artists";
  }

  @PostMapping("/artists/{artistId}/delete")
  public String deleteArtist(@PathVariable Long artistId, RedirectAttributes ra) {
    try {
      adminService.deleteArtist(artistId);
    } catch (BusinessException e) {
      ra.addFlashAttribute("errorMessage", e.getErrorStatus().getMessage());
    }
    return "redirect:/admin/artists";
  }
}
