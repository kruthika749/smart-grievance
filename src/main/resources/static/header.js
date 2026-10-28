document.addEventListener("DOMContentLoaded", function () {

    const headerContainer = document.getElementById("site-header");
    const footerContainer = document.getElementById("site-footer");

    const userRole = sessionStorage.getItem("userRole");

    /* =========================
       COMMON HEADER
       ========================= */

    if (headerContainer) {

        let navigation = `
            <a href="index.html">
                <span class="nav-icon">&#8962;</span>
                Home
            </a>

            <a href="submit.html">
                <span class="nav-icon">&#9776;</span>
                Submit Grievance
            </a>

            <a href="view-grievances.html">
                <span class="nav-icon">&#9632;</span>
                View Grievances
            </a>

            <a href="my-grievances.html">
                <span class="nav-icon">&#9673;</span>
                My Grievances
            </a>
        `;

        // Show Admin link only for admin users
        if (userRole === "ADMIN") {
            navigation += `
                <a href="admin.html">
                    <span class="nav-icon">&#9881;</span>
                    Admin
                </a>
            `;
        }

        headerContainer.innerHTML = `
            <header class="sg-header">

                <div class="sg-header-container">

                    <!-- Brand -->
                    <a href="index.html" class="sg-brand">

                        <img
                            src="logo-symbol.png"
                            alt="Smart Grievance"
                            class="sg-logo"
                        >

                        <div class="sg-brand-text">
                            <div class="sg-brand-title">
                                Smart Grievance
                            </div>

                            <div class="sg-brand-tagline">
                                Your Voice. A Better Tomorrow.
                            </div>
                        </div>

                    </a>

                    <!-- Navigation -->
                    <nav class="sg-nav">
                        ${navigation}
                    </nav>

                    <!-- Right Side -->
                    <div class="sg-header-right">

                        <a href="login.html" class="sg-login">
                            Login
                        </a>

                        <a href="register.html" class="sg-register">
                            Register
                        </a>

                        <span class="sg-tagline-right">
                            People Problems Progress
                        </span>

                    </div>

                </div>

            </header>
        `;
    }


    /* =========================
       COMMON FOOTER
       ========================= */

    if (footerContainer) {

        footerContainer.innerHTML = `
            <footer class="sg-footer">

                <div class="sg-footer-container">

                    <div class="sg-footer-brand">
                        <strong>Smart Grievance</strong>
                        <span>Your Voice. A Better Tomorrow.</span>
                    </div>

                    <div class="sg-footer-links">
                        <a href="index.html">Home</a>
                        <a href="submit.html">Submit Grievance</a>
                        <a href="view-grievances.html">View Grievances</a>
                        <a href="my-grievances.html">My Grievances</a>
                    </div>

                    <div class="sg-footer-copy">
                        © 2026 Smart Grievance. All rights reserved.
                    </div>

                </div>

            </footer>
        `;
    }

});