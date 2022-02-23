import {Component, Inject, OnDestroy, OnInit, Renderer2} from '@angular/core'
import {OperationService, OperationState} from './operation/operation.service'
import {Subject} from 'rxjs'
import {DOCUMENT} from '@angular/common'
import {InfoService} from './info/info.service'
import {takeUntil} from 'rxjs/operators'
import {Router} from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {

  private readonly darkThemeClass = 'dark-theme'
  private readonly unsubscribe$ = new Subject<void>()
  private readonly activeOperation$ = this.operationService.isActiveOperation()

  constructor(
    @Inject(DOCUMENT) private readonly document: Document,
    private readonly renderer: Renderer2,
    private readonly infoService: InfoService,
    private readonly operationService: OperationService,
    private readonly router: Router
  ) {
  }

  ngOnInit() {
    this.infoService
      .isDarkTheme()
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe((isDarkTheme) => {
        if (isDarkTheme) {
          this.renderer.addClass(this.document.body, this.darkThemeClass)
        } else {
          this.renderer.removeClass(this.document.body, this.darkThemeClass)
        }
      })

    this.activeOperation$
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe(state => {
        switch (state) {
          case OperationState.ACTIVE:
            this.router.navigate(['/operation'])
            break;
          case OperationState.PROCESSING:
            this.router.navigate(['/operation-processing'])
            break;
          case OperationState.VOID:
          default:
            this.router.navigate(['/'])
            break;
        }
      })
  }

  ngOnDestroy() {
    this.unsubscribe$.next()
    this.unsubscribe$.complete()
  }
}
