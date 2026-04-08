// =============================================
//  SSARTZ GALLERY — MAIN JS (Fixed Version)
// =============================================

// --- Mobile Nav Toggle ---
// Inga thaan change pannirukkaen: direct element target panni toggle logic
function toggleMenu() {
  const navLinks = document.querySelector('.nav-links');
  const hamburger = document.querySelector('.hamburger');
  if (navLinks && hamburger) {
    navLinks.classList.toggle('open');
    hamburger.classList.toggle('active'); // Hamburger 'X' maara idhu venum
  }
}

// Close mobile nav on outside click
document.addEventListener('click', function (e) {
  const nav = document.querySelector('.navbar');
  const navLinks = document.querySelector('.nav-links');
  const hamburger = document.querySelector('.hamburger');

  if (nav && !nav.contains(e.target)) {
    if (navLinks && navLinks.classList.contains('open')) {
      navLinks.classList.remove('open');
      if (hamburger) hamburger.classList.remove('active');
    }
  }
});

// --- Init all ---
document.addEventListener('DOMContentLoaded', function () {
  // 🚩 MUKKIYAM: Hamburger-ku event listener manual-ah add pannuvom
  const burgerBtn = document.getElementById('hamburger');
  if (burgerBtn) {
    burgerBtn.addEventListener('click', function(e) {
        e.preventDefault();
        toggleMenu();
    });
  }

  revealOnScroll();
  initCartButtons();
  autoHideSuccess();
  handleImgErrors();
  setActiveNav();
});

// --- Bakki ellam unga original code apdiye irukku (Scroll, Cart, etc.) ---
window.addEventListener('scroll', function () {
  const navbar = document.querySelector('.navbar');
  if (!navbar) return;
  if (window.scrollY > 10) {
    navbar.style.boxShadow = '0 2px 20px rgba(0,0,0,0.07)';
  } else {
    navbar.style.boxShadow = 'none';
  }
});

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

function handleImgErrors() {
  document.querySelectorAll('img').forEach((img) => {
    img.addEventListener('error', function () {
      this.src = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='400' height='300'%3E%3Crect fill='%23f5f4f0' width='400' height='300'/%3E%3Ctext fill='%23aaa' font-family='Georgia' font-size='18' x='50%25' y='50%25' text-anchor='middle' dominant-baseline='middle'%3EImage%3C/text%3E%3C/svg%3E";
    });
  });
}

function setActiveNav() {
  const path = window.location.pathname;
  document.querySelectorAll('.nav-links a').forEach((link) => {
    const href = link.getAttribute('href');
    if (href === path || (href !== '/' && path.startsWith(href))) {
      link.classList.add('active');
    }
  });
}