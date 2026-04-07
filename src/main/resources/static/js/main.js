// =============================================
//  SSARTZ GALLERY — MAIN JS
// =============================================

// --- Mobile Nav Toggle ---
function toggleMenu() {
  const navLinks = document.querySelector('.nav-links');
  navLinks.classList.toggle('open');
}

// Close mobile nav on outside click
document.addEventListener('click', function (e) {
  const nav = document.querySelector('.navbar');
  if (nav && !nav.contains(e.target)) {
    const navLinks = document.querySelector('.nav-links');
    if (navLinks) navLinks.classList.remove('open');
  }
});

// --- Navbar scroll shadow ---
window.addEventListener('scroll', function () {
  const navbar = document.querySelector('.navbar');
  if (!navbar) return;
  if (window.scrollY > 10) {
    navbar.style.boxShadow = '0 2px 20px rgba(0,0,0,0.07)';
  } else {
    navbar.style.boxShadow = 'none';
  }
});

// --- Scroll reveal for artwork cards ---
function revealOnScroll() {
  const cards = document.querySelectorAll('.artwork-card, .cat-card');
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry, i) => {
        if (entry.isIntersecting) {
          setTimeout(() => {
            entry.target.style.opacity = '1';
            entry.target.style.transform = 'translateY(0)';
          }, i * 80);
          observer.unobserve(entry.target);
        }
      });
    },
    { threshold: 0.1 }
  );

  cards.forEach((card) => {
    card.style.opacity = '0';
    card.style.transform = 'translateY(24px)';
    card.style.transition = 'opacity 0.5s ease, transform 0.5s ease, box-shadow 0.3s ease';
    observer.observe(card);
  });
}

// --- Cart button feedback (add to cart) ---
function initCartButtons() {
  document.querySelectorAll('form').forEach((form) => {
    const btn = form.querySelector('.add-cart-btn, .btn-primary');
    if (!btn) return;
    form.addEventListener('submit', function () {
      btn.textContent = 'Adding...';
      btn.style.opacity = '0.7';
    });
  });
}

// --- Flash success message auto-hide ---
function autoHideSuccess() {
  const banner = document.querySelector('.success-banner');
  if (banner) {
    setTimeout(() => {
      banner.style.transition = 'opacity 0.5s ease';
      banner.style.opacity = '0';
      setTimeout(() => banner.remove(), 500);
    }, 4000);
  }
}

// --- Image lazy load fallback ---
function handleImgErrors() {
  document.querySelectorAll('img').forEach((img) => {
    img.addEventListener('error', function () {
      this.src =
        "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='400' height='300'%3E%3Crect fill='%23f5f4f0' width='400' height='300'/%3E%3Ctext fill='%23aaa' font-family='Georgia' font-size='18' x='50%25' y='50%25' text-anchor='middle' dominant-baseline='middle'%3EImage%3C/text%3E%3C/svg%3E";
    });
  });
}

// --- Active nav link highlight (also handled server-side) ---
function setActiveNav() {
  const path = window.location.pathname;
  document.querySelectorAll('.nav-links a').forEach((link) => {
    const href = link.getAttribute('href');
    if (href === path || (href !== '/' && path.startsWith(href))) {
      link.classList.add('active');
    }
  });
}

// --- Init all ---
document.addEventListener('DOMContentLoaded', function () {
  revealOnScroll();
  initCartButtons();
  autoHideSuccess();
  handleImgErrors();
  setActiveNav();
});