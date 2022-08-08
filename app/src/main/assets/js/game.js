var notClicked = true;
function animated(param,answer) {
    if (notClicked) {
        notClicked = false;
         param.classList.add('animate__animated', 'animate__bounceIn');
        param.addEventListener('animationend', () => {
            let p = param.firstElementChild;
            if (p.innerText.trim() == answer.trim()) {
            p.classList.add('bg-success','border-success','text-white');
            } else {
             p.classList.add('bg-danger','border-danger','text-white');   
            }
            param.lastElementChild.style.display = "block";
            
            document.querySelectorAll('.option').forEach(value => {
            if (value.innerText.trim() == answer) {
            value.classList.add('border-success');
            }
                document.getElementById('next').style.display = "block";
            });
        });
    }
}